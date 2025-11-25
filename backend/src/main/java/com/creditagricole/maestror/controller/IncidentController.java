package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.entity.Incident;
import com.creditagricole.maestror.repository.IncidentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "API for managing operational risk incidents")
@CrossOrigin(origins = "*")
public class IncidentController {

    private final IncidentRepository repository;

    @GetMapping
    @Operation(summary = "Get all incidents")
    public ResponseEntity<List<Incident>> getAllIncidents() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get incident by code")
    public ResponseEntity<Incident> getIncidentByCode(@PathVariable String code) {
        return repository.findByIncidentCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get incidents by status")
    public ResponseEntity<List<Incident>> getIncidentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(repository.findByStatus(status));
    }

    @GetMapping("/entity/{entityCode}")
    @Operation(summary = "Get incidents by entity")
    public ResponseEntity<List<Incident>> getIncidentsByEntity(@PathVariable String entityCode) {
        return ResponseEntity.ok(repository.findByEntityCode(entityCode));
    }

    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get incidents by severity")
    public ResponseEntity<List<Incident>> getIncidentsBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(repository.findBySeverity(severity));
    }
}
