package com.creditagricole.maestror.schema.reader;

import com.creditagricole.maestror.schema.model.SchemaColumn;
import com.creditagricole.maestror.schema.model.SchemaTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Service de lecture du fichier Excel Modeles_Mappings.xlsx
 */
@Slf4j
@Service
public class ExcelSchemaReader {
    
    private static final String MODELE_STAGING_SHEET = "MODELE_STAGING";
    private static final int COL_NOM_TABLE = 0;
    private static final int COL_NOM_CHAMP = 1;
    private static final int COL_TYPE_CHAMPS = 2;
    private static final int COL_TAILLE_CHAMPS = 3;
    private static final int COL_CLE_PRIMAIRE = 4;
    private static final int COL_CLE_ETRANGERE = 5;
    private static final int COL_DESCRIPTION = 6;
    
    /**
     * Lit l'onglet MODELE_STAGING et retourne les tables
     */
    public List<SchemaTable> readModeleStagingSheet(InputStream inputStream) throws IOException {
        Map<String, SchemaTable> tables = new LinkedHashMap<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheet(MODELE_STAGING_SHEET);
            
            if (sheet == null) {
                log.warn("Onglet {} non trouvé dans le fichier Excel", MODELE_STAGING_SHEET);
                return new ArrayList<>();
            }
            
            // Parcourir les lignes (en ignorant l'en-tête)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Vérifier si la ligne est vide
                if (isRowEmpty(row)) continue;
                
                String nomTable = getCellValue(row.getCell(COL_NOM_TABLE));
                String nomChamp = getCellValue(row.getCell(COL_NOM_CHAMP));
                String typeChamps = getCellValue(row.getCell(COL_TYPE_CHAMPS));
                String tailleChamps = getCellValue(row.getCell(COL_TAILLE_CHAMPS));
                String clePrimaire = getCellValue(row.getCell(COL_CLE_PRIMAIRE));
                String cleEtrangere = getCellValue(row.getCell(COL_CLE_ETRANGERE));
                String description = getCellValue(row.getCell(COL_DESCRIPTION));
                
                // Créer ou récupérer la table
                SchemaTable table = tables.computeIfAbsent(nomTable, k -> 
                    SchemaTable.builder().nomTable(nomTable).build()
                );
                
                // Créer la colonne
                SchemaColumn column = SchemaColumn.builder()
                    .nomChamp(nomChamp)
                    .typeChamps(typeChamps)
                    .tailleChamps(tailleChamps)
                    .clePrimaire("Oui".equalsIgnoreCase(clePrimaire))
                    .cleEtrangere(cleEtrangere)
                    .description(description)
                    .build();
                
                table.getColumns().add(column);
                
                log.debug("Colonne lue: table={}, colonne={}, type={}", nomTable, nomChamp, typeChamps);
            }
        }
        
        log.info("Nombre de tables lues: {}", tables.size());
        return new ArrayList<>(tables.values());
    }
    
    /**
     * Récupère la valeur d'une cellule en tant que String
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
    
    /**
     * Vérifie si une ligne est vide
     */
    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
