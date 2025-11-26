package com.creditagricole.maestror.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour interroger les tables de référence de manière générique
 */
@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
@Slf4j
public class TableQueryController {

    private final com.creditagricole.maestror.service.TableQueryService tableQueryService;

    public TableQueryController(com.creditagricole.maestror.service.TableQueryService tableQueryService) {
        this.tableQueryService = tableQueryService;
    }

    /**
     * Liste les tables disponibles pour consultation
     */
    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableTables() {
        log.info("Récupération de la liste des tables disponibles");
        return ResponseEntity.ok(tableQueryService.getAvailableTables());
    }

    /**
     * Récupère les colonnes d'une table
     */
    @GetMapping("/{tableName}/columns")
    public ResponseEntity<List<String>> getTableColumns(@PathVariable String tableName) {
        log.info("Récupération des colonnes de la table: {}", tableName);
        return ResponseEntity.ok(tableQueryService.getTableColumns(tableName));
    }

    /**
     * Interroge une table avec filtres optionnels
     */
    @GetMapping("/{tableName}/data")
    public ResponseEntity<List<Map<String, Object>>> queryTable(
            @PathVariable String tableName,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue,
            @RequestParam(defaultValue = "100") int limit) {
        
        log.info("Interrogation de la table {} avec filtre {}={}, limit={}", 
                tableName, filterColumn, filterValue, limit);
        
        List<Map<String, Object>> data = tableQueryService.queryTable(
                tableName, filterColumn, filterValue, limit);
        
        return ResponseEntity.ok(data);
    }
}
