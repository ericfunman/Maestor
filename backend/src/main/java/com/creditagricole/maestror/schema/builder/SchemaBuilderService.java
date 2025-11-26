package com.creditagricole.maestror.schema.builder;

import com.creditagricole.maestror.schema.model.SchemaTable;
import com.creditagricole.maestror.schema.reader.ExcelSchemaReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service de construction du schéma STAGING
 * Lit le fichier Excel Modeles_Mappings.xlsx et génère/met à jour les tables
 */
@Slf4j
@Service
public class SchemaBuilderService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ExcelSchemaReader excelSchemaReader;
    
    @Value("${maestror.schema.excel-path:Modeles_Mappings.xlsx}")
    private String excelPath;
    
    @Value("${maestror.schema.auto-build:true}")
    private boolean autoBuild;
    
    /**
     * Construit le schéma au démarrage de l'application
     */
    @EventListener(ApplicationReadyEvent.class)
    public void buildSchemaOnStartup() {
        if (!autoBuild) {
            log.info("Auto-build des tables STAGING désactivé");
            return;
        }
        
        log.info("Démarrage de la construction du schéma STAGING depuis: {}", excelPath);
        buildSchema();
    }
    
    /**
     * Construit/met à jour le schéma
     */
    public void buildSchema() {
        try {
            List<SchemaTable> tables = readExcelFile();
            
            for (SchemaTable table : tables) {
                if (tableExists(table.getNomTable())) {
                    log.info("Table {} existe déjà, vérification des colonnes manquantes", table.getNomTable());
                    // TODO: Implémenter ALTER TABLE pour ajouter les colonnes manquantes
                } else {
                    log.info("Création de la table {}", table.getNomTable());
                    createTable(table);
                }
            }
            
            log.info("Construction du schéma STAGING terminée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la construction du schéma", e);
            throw new RuntimeException("Erreur lors de la construction du schéma STAGING", e);
        }
    }
    
    /**
     * Lit le fichier Excel
     */
    private List<SchemaTable> readExcelFile() throws IOException {
        Path path = Paths.get(excelPath);
        
        if (!Files.exists(path)) {
            log.warn("Fichier Excel {} non trouvé, tentative du chemin dans le workspace", excelPath);
            // Essayer le chemin relatif au workspace
            path = Paths.get(System.getProperty("user.dir"), excelPath);
        }
        
        if (!Files.exists(path)) {
            throw new IOException("Fichier Excel non trouvé: " + path.toAbsolutePath());
        }
        
        log.info("Lecture du fichier Excel: {}", path.toAbsolutePath());
        
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            return excelSchemaReader.readModeleStagingSheet(fis);
        }
    }
    
    /**
     * Crée une table
     */
    private void createTable(SchemaTable table) {
        String ddl = table.generateCreateTableDDL();
        log.debug("DDL à exécuter: {}", ddl);
        
        try {
            jdbcTemplate.execute(ddl);
            log.info("Table {} créée avec succès", table.getNomTable());
        } catch (Exception e) {
            log.error("Erreur lors de la création de la table {}", table.getNomTable(), e);
            throw new RuntimeException("Erreur lors de la création de la table " + table.getNomTable(), e);
        }
    }
    
    /**
     * Vérifie si une table existe
     */
    private boolean tableExists(String tableName) {
        try {
            Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_name = ?)",
                new Object[]{tableName},
                Boolean.class
            );
            return exists != null && exists;
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification de l'existence de la table {}", tableName, e);
            return false;
        }
    }
    
    /**
     * Ajoute une colonne à une table existante
     */
    public void addColumnToTable(String tableName, String columnName, String columnType) {
        String ddl = String.format(
            "ALTER TABLE \"%s\" ADD COLUMN IF NOT EXISTS \"%s\" %s",
            tableName, columnName, columnType
        );
        
        log.info("Ajout de la colonne {} à la table {}", columnName, tableName);
        
        try {
            jdbcTemplate.execute(ddl);
            log.info("Colonne {} ajoutée avec succès à la table {}", columnName, tableName);
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout de la colonne {} à la table {}", columnName, tableName, e);
            throw new RuntimeException("Erreur lors de l'ajout de la colonne", e);
        }
    }
}
