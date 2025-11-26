package com.creditagricole.maestror.schema.builder;

import com.creditagricole.maestror.schema.model.TableDefinition;
import com.creditagricole.maestror.schema.parser.ExcelModelParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Builder de schéma STAGING
 * Lit le fichier Excel Modeles_Mappings.xlsx et crée/met à jour les tables STAGING dans PostgreSQL
 * 
 * NOTE: Désactivé car SchemaBuilderService est utilisé à la place
 */
// @Component
@Slf4j
public class SchemaStagingBuilder {

    private final JdbcTemplate jdbcTemplate;
    private final ExcelModelParser excelModelParser;
    private final String excelFilePath;

    @Autowired
    public SchemaStagingBuilder(JdbcTemplate jdbcTemplate, ExcelModelParser excelModelParser,
                               @Value("${schema.modeles-mappings-file:./Modeles_Mappings.xlsx}") String excelFilePath) {
        this.jdbcTemplate = jdbcTemplate;
        this.excelModelParser = excelModelParser;
        this.excelFilePath = excelFilePath;
    }

    /**
     * Construit le schéma STAGING au démarrage de l'application
     */
    @EventListener(ApplicationReadyEvent.class)
    public void buildStagingSchema() {
        try {
            log.info("Starting STAGING schema build from Excel file: {}", excelFilePath);

            List<TableDefinition> tables = excelModelParser.parseModeleStagingSheet(excelFilePath);

            if (tables.isEmpty()) {
                log.warn("No tables found in Excel file");
                return;
            }

            for (TableDefinition table : tables) {
                createOrUpdateTable(table);
            }

            log.info("STAGING schema build completed successfully");
        } catch (IOException e) {
            log.error("Error building STAGING schema", e);
            throw new RuntimeException("Failed to build STAGING schema", e);
        }
    }

    /**
     * Crée ou met à jour une table STAGING
     */
    private void createOrUpdateTable(TableDefinition table) {
        try {
            log.info("Processing table: {}", table.getTableName());

            // Vérifier si la table existe
            boolean tableExists = tableExists(table.getTableName());

            if (tableExists) {
                log.info("Table {} already exists. Performing ALTER TABLE if needed", table.getTableName());
                // TODO: Implémenter la logique de migration (ALTER TABLE)
                // Pour l'instant, on ne fait rien si la table existe
            } else {
                // Créer la table
                String createTableSql = table.toCreateTableSql();
                log.debug("Executing SQL:\n{}", createTableSql);

                jdbcTemplate.execute(createTableSql);
                log.info("Table {} created successfully", table.getTableName());
            }
        } catch (Exception e) {
            log.error("Error processing table: {}", table.getTableName(), e);
            throw new RuntimeException("Failed to process table: " + table.getTableName(), e);
        }
    }

    /**
     * Vérifie si une table existe
     */
    private boolean tableExists(String tableName) {
        try {
            String sql = "SELECT 1 FROM information_schema.tables WHERE table_name = ? AND table_schema = 'public'";
            List<?> result = jdbcTemplate.queryForList(sql, tableName.toLowerCase());
            return !result.isEmpty();
        } catch (Exception e) {
            log.debug("Error checking if table exists: {}", tableName, e);
            return false;
        }
    }

    /**
     * Supprime toutes les tables STAGING (utile pour les tests)
     */
    public void dropAllStagingTables() {
        try {
            log.warn("Dropping all STAGING tables");

            List<TableDefinition> tables = excelModelParser.parseModeleStagingSheet(excelFilePath);

            for (TableDefinition table : tables) {
                String dropTableSql = table.toDropTableSql();
                log.debug("Executing SQL:\n{}", dropTableSql);

                jdbcTemplate.execute(dropTableSql);
                log.info("Table {} dropped successfully", table.getTableName());
            }
        } catch (IOException e) {
            log.error("Error dropping STAGING tables", e);
            throw new RuntimeException("Failed to drop STAGING tables", e);
        }
    }
}
