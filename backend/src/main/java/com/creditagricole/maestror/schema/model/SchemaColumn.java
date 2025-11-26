package com.creditagricole.maestror.schema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une colonne de la définition de schéma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemaColumn {
    private String nomChamp;           // NOM_CHAMP
    private String typeChamps;         // TYPE_CHAMPS (VARCHAR, INTEGER, DECIMAL, DATE, etc.)
    private String tailleChamps;       // TAILLE_CHAMPS (255, 15,2, etc.)
    private boolean clePrimaire;       // CLE_PRIMAIRE (Oui/Non)
    private String cleEtrangere;       // CLE_ETRANGERE (table.colonne)
    private String description;        // DESCRIPTION
    
    /**
     * Génère le DDL pour cette colonne
     */
    public String toDDL() {
        StringBuilder ddl = new StringBuilder();
        
        // Nom de la colonne
        ddl.append("\"").append(nomChamp).append("\"").append(" ");
        
        // Type et taille - mapper les types spéciaux
        String mappedType = mapType(typeChamps);
        
        if ("VARCHAR".equalsIgnoreCase(mappedType) || "CHAR".equalsIgnoreCase(mappedType)) {
            ddl.append(mappedType).append("(").append(tailleChamps).append(")");
        } else if ("DECIMAL".equalsIgnoreCase(mappedType) || "NUMERIC".equalsIgnoreCase(mappedType)) {
            ddl.append(mappedType).append("(").append(tailleChamps).append(")");
        } else if ("SERIAL".equalsIgnoreCase(mappedType) || "BIGSERIAL".equalsIgnoreCase(mappedType)) {
            ddl.append(mappedType);
        } else {
            ddl.append(mappedType);
        }
        
        // Clé primaire
        if (clePrimaire) {
            if (!("SERIAL".equalsIgnoreCase(mappedType) || "BIGSERIAL".equalsIgnoreCase(mappedType))) {
                ddl.append(" PRIMARY KEY");
            }
        }
        
        // Contrainte NOT NULL par défaut sauf pour PK et FK
        if (!clePrimaire && !isValidForeignKey()) {
            ddl.append(" NOT NULL");
        }
        
        return ddl.toString();
    }
    
    /**
     * Mappe les types du fichier Excel vers les types PostgreSQL
     */
    private String mapType(String type) {
        if (type == null || type.isEmpty()) {
            return "VARCHAR";
        }
        return switch (type.toUpperCase()) {
            case "ID", "INT", "INTEGER" -> "SERIAL";
            case "VARCHAR2" -> "VARCHAR";
            case "DATE" -> "TIMESTAMP";
            case "DECIMAL" -> "NUMERIC";
            case "BIGINT" -> "BIGSERIAL";
            case "TEXT" -> "TEXT";
            case "BOOLEAN", "BOOL" -> "BOOLEAN";
            default -> type.toUpperCase();
        };
    }
    
    /**
     * Vérifie si la clé étrangère est valide (pas vide et pas "Non")
     */
    public boolean isValidForeignKey() {
        return cleEtrangere != null && !cleEtrangere.isEmpty() && !"Non".equalsIgnoreCase(cleEtrangere);
    }
}
