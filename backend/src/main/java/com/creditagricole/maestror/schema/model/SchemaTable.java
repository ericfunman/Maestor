package com.creditagricole.maestror.schema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente la définition d'une table STAGING
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemaTable {
    private String nomTable;           // NOM_TABLE (avec préfixe)
    @Builder.Default
    private List<SchemaColumn> columns = new ArrayList<>();
    
    /**
     * Génère le DDL de création de table
     */
    public String generateCreateTableDDL() {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE IF NOT EXISTS \"").append(nomTable).append("\" (\n");
        
        for (int i = 0; i < columns.size(); i++) {
            SchemaColumn col = columns.get(i);
            ddl.append("  ").append(col.toDDL());
            
            if (i < columns.size() - 1) {
                ddl.append(",\n");
            }
        }
        
        // Ajouter les contraintes de clés étrangères (seulement si valides)
        boolean hasFK = false;
        for (SchemaColumn col : columns) {
            if (col.isValidForeignKey()) {
                if (!hasFK) {
                    ddl.append(",\n");
                    hasFK = true;
                }
                ddl.append("  CONSTRAINT fk_").append(nomTable).append("_").append(col.getNomChamp())
                   .append(" FOREIGN KEY (\"").append(col.getNomChamp()).append("\") ")
                   .append("REFERENCES ").append(col.getCleEtrangere()).append("\n");
            }
        }
        
        ddl.append("\n);\n");
        return ddl.toString();
    }
    
    /**
     * Vérifie si la table existe
     */
    public String checkTableExistsDDL() {
        return "SELECT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_name = '" + nomTable + "');";
    }
    
    /**
     * Récupère les colonnes existantes
     */
    public String getExistingColumnsDDL() {
        return "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '" + nomTable + "';";
    }
}
