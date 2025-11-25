package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.entity.Control;
import com.creditagricole.maestror.repository.ControlRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/controls")
@RequiredArgsConstructor
@Tag(name = "Controls", description = "API for managing operational risk controls")
@CrossOrigin(origins = "*")
public class ControlController {

    private final ControlRepository repository;

    @GetMapping
    @Operation(summary = "Get all controls")
    public ResponseEntity<List<Control>> getAllControls() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get control by ID")
    public ResponseEntity<Control> getControlById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get control by code")
    public ResponseEntity<Control> getControlByCode(@PathVariable String code) {
        return repository.findByControlCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get controls by status")
    public ResponseEntity<List<Control>> getControlsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(repository.findByStatus(status));
    }

    @GetMapping("/entity/{entityCode}")
    @Operation(summary = "Get controls by entity")
    public ResponseEntity<List<Control>> getControlsByEntity(@PathVariable String entityCode) {
        return ResponseEntity.ok(repository.findByEntityCode(entityCode));
    }

    @GetMapping("/type/{controlType}")
    @Operation(summary = "Get controls by type")
    public ResponseEntity<List<Control>> getControlsByType(@PathVariable String controlType) {
        return ResponseEntity.ok(repository.findByControlType(controlType));
    }
}
