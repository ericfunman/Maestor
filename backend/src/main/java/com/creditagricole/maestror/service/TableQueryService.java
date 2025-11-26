package com.creditagricole.maestror.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour interroger les tables de manière générique
 */
@Service
@Slf4j
public class TableQueryService {

    private static final List<String> ALLOWED_TABLES = Arrays.asList(
            "test_data",
            "PARAM_FICHIER"
    );

    private final JdbcTemplate jdbcTemplate;

    public TableQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retourne la liste des tables autorisées
     */
    public List<String> getAvailableTables() {
        return new ArrayList<>(ALLOWED_TABLES);
    }

    /**
     * Récupère les colonnes d'une table
     */
    public List<String> getTableColumns(String tableName) {
        validateTableName(tableName);
        
        String sql = "SELECT column_name FROM information_schema.columns " +
                    "WHERE table_name = ? AND table_schema = 'public' " +
                    "ORDER BY ordinal_position";
        
        return jdbcTemplate.queryForList(sql, String.class, tableName);
    }

    /**
     * Interroge une table avec filtres optionnels
     */
    public List<Map<String, Object>> queryTable(String tableName, String filterColumn, 
                                                 String filterValue, int limit) {
        validateTableName(tableName);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        
        // Utiliser des guillemets si le nom contient des majuscules
        if (tableName.matches(".*[A-Z].*")) {
            sql.append("\"").append(tableName).append("\"");
        } else {
            sql.append(tableName);
        }
        
        List<Object> params = new ArrayList<>();
        
        // Ajouter le filtre si présent
        if (filterColumn != null && !filterColumn.isEmpty() && 
            filterValue != null && !filterValue.isEmpty()) {
            
            List<String> columns = getTableColumns(tableName);
            if (columns.contains(filterColumn)) {
                sql.append(" WHERE \"").append(filterColumn).append("\" ILIKE ?");
                params.add("%" + filterValue + "%");
            } else {
                log.warn("Colonne de filtre {} non trouvée dans la table {}", filterColumn, tableName);
            }
        }
        
        sql.append(" LIMIT ?");
        params.add(limit);
        
        log.debug("Exécution de la requête: {}", sql);
        
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    /**
     * Valide que le nom de table est autorisé
     */
    private void validateTableName(String tableName) {
        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("Table non autorisée: " + tableName);
        }
    }
}
