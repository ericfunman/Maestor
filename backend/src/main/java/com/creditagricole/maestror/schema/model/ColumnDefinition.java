package com.creditagricole.maestror.schema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représentation d'une colonne de table STAGING
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDefinition {
    private static final String TYPE_VARCHAR = "VARCHAR";
    private static final String TYPE_VARCHAR2 = "VARCHAR2";
    private static final String TYPE_NUMERIC = "NUMERIC";
    private static final String TYPE_DATE = "Date";
    private static final String TYPE_DECIMAL = "DECIMAL";
    
    private String columnName;
    private String dataType;
    private String size;
    private Boolean isPrimaryKey;
    private String foreignKey;
    private String description;

    /**
     * Génère la clause SQL pour cette colonne
     */
    public String toSqlDefinition() {
        StringBuilder sql = new StringBuilder();
        sql.append(columnName).append(" ");

        // Type et taille
        sql.append(mapPostgreSQLType(dataType));

        // Taille si applicable
        if (size != null && !size.isEmpty() && !dataType.equalsIgnoreCase(TYPE_DATE) 
            && (dataType.equalsIgnoreCase(TYPE_VARCHAR2) || dataType.equalsIgnoreCase(TYPE_DECIMAL) || dataType.equalsIgnoreCase(TYPE_NUMERIC))) {
            sql.append("(").append(size).append(")");
        }

        // Clé primaire
        if (isPrimaryKey != null && isPrimaryKey) {
            sql.append(" PRIMARY KEY");
            // Si INT et PK, c'est un auto-increment
            if (dataType.equalsIgnoreCase("ID") || dataType.equalsIgnoreCase("INT")) {
                sql.insert(columnName.length() + 1, "SERIAL ");
            }
        }

        // Nullable (par défaut NOT NULL sauf si explicitement nullable)
        if (isPrimaryKey == null || !isPrimaryKey) {
            sql.append(" NOT NULL");
        }

        return sql.toString();
    }

    /**
     * Mappe les types données du fichier Excel aux types PostgreSQL
     */
    private String mapPostgreSQLType(String dataType) {
        return switch (dataType.toUpperCase()) {
            case "ID", "INT", "INTEGER" -> "INTEGER";
            case "VARCHAR2", "VARCHAR" -> TYPE_VARCHAR;
            case "DATE" -> "TIMESTAMP";
            case TYPE_DECIMAL, TYPE_NUMERIC -> TYPE_NUMERIC;
            case "BIGINT", "LONG" -> "BIGINT";
            case "TEXT" -> "TEXT";
            case "BOOLEAN", "BOOL" -> "BOOLEAN";
            default -> TYPE_VARCHAR + "(255)";
        };
    }
}
