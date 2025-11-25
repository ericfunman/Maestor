package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.entity.RiskCalculation;
import com.creditagricole.maestror.repository.RiskCalculationRepository;
import com.creditagricole.maestror.service.RiskCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-calculations")
@RequiredArgsConstructor
@Tag(name = "Risk Calculations", description = "API for managing risk calculations")
@CrossOrigin(origins = "*")
public class RiskCalculationController {

    private final RiskCalculationRepository repository;
    private final RiskCalculationService riskCalculationService;

    @GetMapping
    @Operation(summary = "Get all risk calculations")
    public ResponseEntity<List<RiskCalculation>> getAllCalculations() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get risk calculation by ID")
    public ResponseEntity<RiskCalculation> getCalculationById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entity/{entityCode}")
    @Operation(summary = "Get risk calculations by entity")
    public ResponseEntity<List<RiskCalculation>> getCalculationsByEntity(@PathVariable String entityCode) {
        return ResponseEntity.ok(repository.findByEntityCode(entityCode));
    }

    @GetMapping("/level/{riskLevel}")
    @Operation(summary = "Get risk calculations by level")
    public ResponseEntity<List<RiskCalculation>> getCalculationsByLevel(@PathVariable String riskLevel) {
        return ResponseEntity.ok(repository.findByRiskLevel(riskLevel));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Trigger manual risk calculation", description = "Manually trigger risk calculation for all active risks")
    public ResponseEntity<String> triggerCalculation() {
        try {
            riskCalculationService.calculateRiskLevels();
            return ResponseEntity.ok("Risk calculation triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/calculate/entity/{entityCode}")
    @Operation(summary = "Calculate risks for specific entity")
    public ResponseEntity<String> calculateForEntity(@PathVariable String entityCode) {
        try {
            riskCalculationService.calculateRiskForEntity(entityCode);
            return ResponseEntity.ok("Risk calculation for entity " + entityCode + " completed");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
