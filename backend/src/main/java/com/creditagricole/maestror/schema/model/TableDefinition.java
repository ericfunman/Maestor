package com.creditagricole.maestror.schema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Représentation d'une table STAGING complète
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableDefinition {
    private String tableName;
    private String description;
    private List<ColumnDefinition> columns;

    /**
     * Génère la clause CREATE TABLE pour cette table
     */
    public String toCreateTableSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        for (int i = 0; i < columns.size(); i++) {
            sql.append("  ").append(columns.get(i).toSqlDefinition());
            if (i < columns.size() - 1) {
                sql.append(",\n");
            }
        }

        sql.append("\n);\n");

        // Ajouter des commentaires
        if (description != null && !description.isEmpty()) {
            sql.append("COMMENT ON TABLE ").append(tableName).append(" IS '").append(description).append("';\n");
        }

        for (ColumnDefinition col : columns) {
            if (col.getDescription() != null && !col.getDescription().isEmpty()) {
                sql.append("COMMENT ON COLUMN ").append(tableName).append(".").append(col.getColumnName())
                        .append(" IS '").append(col.getDescription()).append("';\n");
            }
        }

        return sql.toString();
    }

    /**
     * Génère la clause DROP TABLE (pour tests)
     */
    public String toDropTableSql() {
        return "DROP TABLE IF EXISTS " + tableName + " CASCADE;";
    }
}
