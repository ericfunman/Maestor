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
    private static final String TYPE_VARCHAR = "VARCHAR";
    private static final String TYPE_SERIAL = "SERIAL";
    private static final String TYPE_BIGSERIAL = "BIGSERIAL";
    private static final String TYPE_NUMERIC = "NUMERIC";
    
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
        
        if (TYPE_VARCHAR.equalsIgnoreCase(mappedType) || "CHAR".equalsIgnoreCase(mappedType)) {
            ddl.append(mappedType).append("(").append(tailleChamps).append(")");
        } else if ("DECIMAL".equalsIgnoreCase(mappedType) || TYPE_NUMERIC.equalsIgnoreCase(mappedType)) {
            ddl.append(mappedType).append("(").append(tailleChamps).append(")");
        } else if (TYPE_SERIAL.equalsIgnoreCase(mappedType) || TYPE_BIGSERIAL.equalsIgnoreCase(mappedType)) {
            ddl.append(mappedType);
        } else {
            ddl.append(mappedType);
        }
        
        // Clé primaire (sauf si SERIAL ou BIGSERIAL qui impliquent déjà un auto-increment)
        if (clePrimaire && !(TYPE_SERIAL.equalsIgnoreCase(mappedType) || TYPE_BIGSERIAL.equalsIgnoreCase(mappedType))) {
            ddl.append(" PRIMARY KEY");
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
            return TYPE_VARCHAR;
        }
        return switch (type.toUpperCase()) {
            case "ID", "INT", "INTEGER" -> TYPE_SERIAL;
            case "VARCHAR2" -> TYPE_VARCHAR;
            case "DATE" -> "TIMESTAMP";
            case "DECIMAL" -> TYPE_NUMERIC;
            case "BIGINT" -> TYPE_BIGSERIAL;
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
