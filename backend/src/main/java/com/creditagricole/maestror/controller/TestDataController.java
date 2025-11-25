package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.entity.TestData;
import com.creditagricole.maestror.repository.TestDataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-data")
@RequiredArgsConstructor
@Tag(name = "Test Data", description = "API pour gérer les données de test")
public class TestDataController {

    private final TestDataRepository testDataRepository;

    @GetMapping
    @Operation(summary = "Récupérer toutes les données de test")
    public ResponseEntity<List<TestData>> getAllTestData() {
        return ResponseEntity.ok(testDataRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une donnée de test par ID")
    public ResponseEntity<TestData> getTestDataById(@PathVariable Long id) {
        return testDataRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-file/{fileId}")
    @Operation(summary = "Récupérer les données de test par ID de fichier")
    public ResponseEntity<List<TestData>> getTestDataByFileId(@PathVariable Long fileId) {
        return ResponseEntity.ok(testDataRepository.findByFileStorageId(fileId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une donnée de test")
    public ResponseEntity<Void> deleteTestData(@PathVariable Long id) {
        if (testDataRepository.existsById(id)) {
            testDataRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
