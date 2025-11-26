package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.dto.ReportRequest;
import com.creditagricole.maestror.dto.ReportResponse;
import com.creditagricole.maestror.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@Slf4j
public class ReportController {
    
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody ReportRequest request) {
        log.info("POST /api/reports - Création d'un rapport: {}", request.getName());
        try {
            ReportResponse response = reportService.createReport(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            throw e;
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        log.info("GET /api/reports - Récupération de tous les rapports");
        List<ReportResponse> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        log.info("GET /api/reports/{} - Récupération du rapport", id);
        ReportResponse report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/{id}/execute")
    public ResponseEntity<ReportResponse> executeReport(@PathVariable Long id) {
        log.info("GET /api/reports/{}/execute - Exécution du rapport", id);
        ReportResponse report = reportService.executeReport(id);
        return ResponseEntity.ok(report);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable Long id,
            @RequestBody ReportRequest request) {
        log.info("PUT /api/reports/{} - Mise à jour du rapport", id);
        ReportResponse report = reportService.updateReport(id, request);
        return ResponseEntity.ok(report);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        log.info("DELETE /api/reports/{} - Suppression du rapport", id);
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
