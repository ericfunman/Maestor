package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.entity.OperationalRiskReferential;
import com.creditagricole.maestror.repository.OperationalRiskReferentialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referentials")
@RequiredArgsConstructor
@Tag(name = "Risk Referentials", description = "API for managing operational risk referentials")
@CrossOrigin(origins = "*")
public class ReferentialController {

    private final OperationalRiskReferentialRepository repository;

    @GetMapping
    @Operation(summary = "Get all risk referentials")
    public ResponseEntity<List<OperationalRiskReferential>> getAllReferentials() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get risk referential by ID")
    public ResponseEntity<OperationalRiskReferential> getReferentialById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get risk referential by code")
    public ResponseEntity<OperationalRiskReferential> getReferentialByCode(@PathVariable String code) {
        return repository.findByRiskCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get risk referentials by category")
    public ResponseEntity<List<OperationalRiskReferential>> getReferentialsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(repository.findByRiskCategory(category));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active risk referentials")
    public ResponseEntity<List<OperationalRiskReferential>> getActiveReferentials() {
        return ResponseEntity.ok(repository.findByActive(true));
    }
}
