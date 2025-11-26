package com.creditagricole.maestror.schema.parser;

import com.creditagricole.maestror.schema.model.ColumnDefinition;
import com.creditagricole.maestror.schema.model.TableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Parser pour fichier Excel Modeles_Mappings.xlsx
 * Extrait le modèle STAGING depuis l'onglet MODELE_STAGING
 */
@Slf4j
@Component
public class ExcelModelParser {

    private static final String MODELE_STAGING_SHEET = "MODELE_STAGING";
    private static final int HEADER_ROW = 0;
    private static final int COL_TABLE_NAME = 0;
    private static final int COL_COLUMN_NAME = 1;
    private static final int COL_DATA_TYPE = 2;
    private static final int COL_SIZE = 3;
    private static final int COL_PRIMARY_KEY = 4;
    private static final int COL_FOREIGN_KEY = 5;
    private static final int COL_DESCRIPTION = 6;

    /**
     * Parse le fichier Excel et retourne les définitions de tables
     */
    public List<TableDefinition> parseModeleStagingSheet(String excelFilePath) throws IOException {
        log.info("Parsing Excel file: {}", excelFilePath);

        File file = new File(excelFilePath);
        if (!file.exists()) {
            throw new IOException("Excel file not found: " + excelFilePath);
        }

        Map<String, TableDefinition> tables = new LinkedHashMap<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(MODELE_STAGING_SHEET);
            if (sheet == null) {
                throw new IOException("Sheet '" + MODELE_STAGING_SHEET + "' not found in Excel file");
            }

            // Sauter la ligne d'en-tête
            for (int rowNum = HEADER_ROW + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }

                String tableName = getCellValue(row, COL_TABLE_NAME);
                if (tableName == null || tableName.isEmpty()) {
                    break; // Fin des données
                }

                String columnName = getCellValue(row, COL_COLUMN_NAME);
                String dataType = getCellValue(row, COL_DATA_TYPE);
                String size = getCellValue(row, COL_SIZE);
                String primaryKey = getCellValue(row, COL_PRIMARY_KEY);
                String foreignKey = getCellValue(row, COL_FOREIGN_KEY);
                String description = getCellValue(row, COL_DESCRIPTION);

                // Créer ou récupérer la table
                TableDefinition table = tables.computeIfAbsent(tableName, k -> {
                    TableDefinition td = new TableDefinition();
                    td.setTableName(tableName);
                    td.setColumns(new ArrayList<>());
                    return td;
                });

                // Ajouter la colonne
                ColumnDefinition column = ColumnDefinition.builder()
                        .columnName(columnName)
                        .dataType(dataType)
                        .size(size)
                        .isPrimaryKey("Oui".equalsIgnoreCase(primaryKey))
                        .foreignKey(foreignKey)
                        .description(description)
                        .build();

                table.getColumns().add(column);

                log.debug("Added column: {}.{} ({})", tableName, columnName, dataType);
            }
        }

        List<TableDefinition> result = new ArrayList<>(tables.values());
        log.info("Parsed {} table(s) from Excel", result.size());
        return result;
    }

    /**
     * Récupère la valeur d'une cellule en tant que String
     */
    private String getCellValue(Row row, int columnIndex) {
        try {
            if (row.getCell(columnIndex) == null) {
                return null;
            }

            return switch (row.getCell(columnIndex).getCellType()) {
                case STRING -> row.getCell(columnIndex).getStringCellValue();
                case NUMERIC -> String.valueOf((int) row.getCell(columnIndex).getNumericCellValue());
                case BOOLEAN -> String.valueOf(row.getCell(columnIndex).getBooleanCellValue());
                default -> null;
            };
        } catch (Exception e) {
            log.warn("Error reading cell at row {}, column {}: {}", row.getRowNum(), columnIndex, e.getMessage());
            return null;
        }
    }
}
