package com.creditagricole.maestror.service;

import com.creditagricole.maestror.entity.*;
import com.creditagricole.maestror.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {

    // Issue #5 et #6 - Constantes pour litteraux dupliques
    private static final String RISK_LEVEL_MEDIUM = "MEDIUM";
    private static final String ERROR_LOG_MESSAGE = "Error processing row {}: {}";

    private final FileStorageRepository fileStorageRepository;
    private final OperationalRiskReferentialRepository riskReferentialRepository;
    private final IncidentRepository incidentRepository;
    private final ControlRepository controlRepository;
    private final TestDataRepository testDataRepository;

    @Transactional
    public void processFile(FileStorage fileStorage) {
        log.info("Starting to process file: {} (ID: {})", fileStorage.getFileName(), fileStorage.getId());
        
        fileStorage.setStatus("PROCESSING");
        fileStorageRepository.save(fileStorage);
        
        try {
            List<String[]> records;
            
            if ("CSV".equals(fileStorage.getFileType())) {
                records = parseCSV(fileStorage.getFileContent());
            } else if ("EXCEL".equals(fileStorage.getFileType())) {
                records = parseExcel(fileStorage.getFileContent());
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + fileStorage.getFileType());
            }
            
            // Pour TEST, pas d'en-tête - compter toutes les lignes
            if ("TEST".equals(fileStorage.getCategory())) {
                fileStorage.setTotalRecords(records.size());
            } else {
                fileStorage.setTotalRecords(records.size() - 1); // Exclude header pour les autres
            }
            
            // Process based on category
            switch (fileStorage.getCategory()) {
                case "REFERENTIAL" -> processReferentialData(records, fileStorage);
                case "INCIDENT" -> processIncidentData(records, fileStorage);
                case "CONTROL" -> processControlData(records, fileStorage);
                case "TEST" -> processTestData(records, fileStorage);
                default -> throw new IllegalArgumentException("Unknown category: " + fileStorage.getCategory());
            }
            
            fileStorage.setStatus("COMPLETED");
            fileStorage.setProcessedAt(LocalDateTime.now());
            log.info("File processing completed: {} (ID: {})", fileStorage.getFileName(), fileStorage.getId());
            
        } catch (Exception e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            fileStorage.setStatus("FAILED");
            fileStorage.setErrorMessage(e.getMessage());
        } finally {
            fileStorageRepository.save(fileStorage);
        }
    }

    private List<String[]> parseCSV(byte[] content) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(content)))) {
            return reader.readAll();
        }
    }

    private List<String[]> parseExcel(byte[] content) throws IOException {
        List<String[]> records = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                List<String> values = new ArrayList<>();
                for (Cell cell : row) {
                    values.add(getCellValueAsString(cell));
                }
                records.add(values.toArray(new String[0]));
            }
        }
        
        return records;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private void processReferentialData(List<String[]> records, FileStorage fileStorage) {
        int processed = 0;
        int failed = 0;
        
        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            try {
                String[] row = records.get(i);
                
                // Issue #6 - Utiliser constante
                OperationalRiskReferential risk = OperationalRiskReferential.builder()
                        .riskCode(row[0])
                        .riskName(row[1])
                        .riskDescription(row.length > 2 ? row[2] : "")
                        .riskCategory(row.length > 3 ? row[3] : "")
                        .riskType(row.length > 4 ? row[4] : "")
                        .businessLine(row.length > 5 ? row[5] : "")
                        .impactLevel(row.length > 6 ? row[6] : RISK_LEVEL_MEDIUM)
                        .probabilityLevel(row.length > 7 ? row[7] : RISK_LEVEL_MEDIUM)
                        .active(true)
                        .fileStorage(fileStorage)
                        .build();
                
                riskReferentialRepository.save(risk);
                processed++;
                
            } catch (Exception e) {
                // Issue #5 - Utiliser constante
                log.error(ERROR_LOG_MESSAGE, i, e.getMessage());
                failed++;
            }
        }
        
        fileStorage.setProcessedRecords(processed);
        fileStorage.setFailedRecords(failed);
    }

    // Issue #7 - Extraire methode pour reduire complexite cognitive
    private void processIncidentData(List<String[]> records, FileStorage fileStorage) {
        int processed = 0;
        int failed = 0;
        
        for (int i = 1; i < records.size(); i++) {
            try {
                Incident incident = buildIncidentFromRow(records.get(i), fileStorage);
                incidentRepository.save(incident);
                processed++;
            } catch (Exception e) {
                log.error(ERROR_LOG_MESSAGE, i, e.getMessage());
                failed++;
            }
        }
        
        fileStorage.setProcessedRecords(processed);
        fileStorage.setFailedRecords(failed);
    }
    
    private Incident buildIncidentFromRow(String[] row, FileStorage fileStorage) {
        return Incident.builder()
                .incidentCode(row[0])
                .incidentTitle(row[1])
                .incidentDescription(row.length > 2 ? row[2] : "")
                .incidentDate(row.length > 3 ? LocalDate.parse(row[3]) : LocalDate.now())
                .severity(row.length > 4 ? row[4] : RISK_LEVEL_MEDIUM)
                .status(row.length > 5 ? row[5] : "OPEN")
                .entityCode(row.length > 6 ? row[6] : "")
                .businessUnit(row.length > 7 ? row[7] : "")
                .financialImpact(row.length > 8 && !row[8].isEmpty() ? new BigDecimal(row[8]) : BigDecimal.ZERO)
                .currency(row.length > 9 ? row[9] : "EUR")
                .detectedBy(row.length > 10 ? row[10] : "")
                .fileStorage(fileStorage)
                .build();
    }

    // Issue #8 - Extraire methode pour reduire complexite cognitive
    private void processControlData(List<String[]> records, FileStorage fileStorage) {
        int processed = 0;
        int failed = 0;
        
        for (int i = 1; i < records.size(); i++) {
            try {
                Control control = buildControlFromRow(records.get(i), fileStorage);
                controlRepository.save(control);
                processed++;
            } catch (Exception e) {
                log.error(ERROR_LOG_MESSAGE, i, e.getMessage());
                failed++;
            }
        }
        
        fileStorage.setProcessedRecords(processed);
        fileStorage.setFailedRecords(failed);
    }
    
    private Control buildControlFromRow(String[] row, FileStorage fileStorage) {
        return Control.builder()
                .controlCode(row[0])
                .controlName(row[1])
                .controlDescription(row.length > 2 ? row[2] : "")
                .controlType(row.length > 3 ? row[3] : "DETECTIVE")
                .frequency(row.length > 4 ? row[4] : "MONTHLY")
                .entityCode(row.length > 5 ? row[5] : "")
                .responsiblePerson(row.length > 6 ? row[6] : "")
                .status(row.length > 7 ? row[7] : "ACTIVE")
                .effectiveness(row.length > 8 ? row[8] : "EFFECTIVE")
                .fileStorage(fileStorage)
                .build();
    }

    private void processTestData(List<String[]> records, FileStorage fileStorage) {
        int processed = 0;
        int failed = 0;
        
        // Pour TEST, pas d'en-tête - traiter toutes les lignes depuis i=0
        for (int i = 0; i < records.size(); i++) {
            try {
                String[] row = records.get(i);
                
                // Prendre le premier champ comme données texte
                String textData = row.length > 0 ? row[0] : "";
                
                // Limiter à 500 caractères
                if (textData.length() > 500) {
                    textData = textData.substring(0, 500);
                }
                
                TestData testData = TestData.builder()
                        .textData(textData)
                        .fileStorage(fileStorage)
                        .build();
                
                testDataRepository.save(testData);
                processed++;
                
            } catch (Exception e) {
                log.error("Error processing row {}: {}", i, e.getMessage());
                failed++;
            }
        }
        
        fileStorage.setProcessedRecords(processed);
        fileStorage.setFailedRecords(failed);
    }
}
