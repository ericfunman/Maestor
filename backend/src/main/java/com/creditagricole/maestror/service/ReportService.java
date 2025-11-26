package com.creditagricole.maestror.service;

import com.creditagricole.maestror.dto.ReportRequest;
import com.creditagricole.maestror.dto.ReportResponse;
import com.creditagricole.maestror.entity.Report;
import com.creditagricole.maestror.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final JdbcTemplate jdbcTemplate;
    
    public ReportService(ReportRepository reportRepository, JdbcTemplate jdbcTemplate) {
        this.reportRepository = reportRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Transactional
    public ReportResponse createReport(ReportRequest request) {
        log.info("Création d'un nouveau rapport: {}", request.getName());
        
        // Validation basique de la requête SQL (interdire DELETE, DROP, UPDATE, INSERT)
        validateSqlQuery(request.getSqlQuery());
        
        Report report = Report.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sqlQuery(request.getSqlQuery())
                .chartType(request.getChartType())
                .xAxisColumn(request.getXAxisColumn())
                .yAxisColumn(request.getYAxisColumn())
                .build();
        
        Report savedReport = reportRepository.save(report);
        
        return mapToResponse(savedReport, null);
    }
    
    @Transactional
    public ReportResponse updateReport(Long id, ReportRequest request) {
        log.info("Mise à jour du rapport ID: {}", id);
        
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rapport non trouvé avec l'ID: " + id));
        
        validateSqlQuery(request.getSqlQuery());
        
        report.setName(request.getName());
        report.setDescription(request.getDescription());
        report.setSqlQuery(request.getSqlQuery());
        report.setChartType(request.getChartType());
        report.setXAxisColumn(request.getXAxisColumn());
        report.setYAxisColumn(request.getYAxisColumn());
        
        Report updatedReport = reportRepository.save(report);
        
        return mapToResponse(updatedReport, null);
    }
    
    public List<ReportResponse> getAllReports() {
        log.info("Récupération de tous les rapports");
        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();
        
        List<ReportResponse> responses = new ArrayList<>();
        for (Report report : reports) {
            responses.add(mapToResponse(report, null));
        }
        return responses;
    }
    
    public ReportResponse getReportById(Long id) {
        log.info("Récupération du rapport ID: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rapport non trouvé avec l'ID: " + id));
        
        return mapToResponse(report, null);
    }
    
    public ReportResponse executeReport(Long id) {
        log.info("Exécution du rapport ID: {}", id);
        
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rapport non trouvé avec l'ID: " + id));
        
        List<Map<String, Object>> data = executeQuery(report.getSqlQuery());
        
        return mapToResponse(report, data);
    }
    
    @Transactional
    public void deleteReport(Long id) {
        log.info("Suppression du rapport ID: {}", id);
        
        if (!reportRepository.existsById(id)) {
            throw new IllegalArgumentException("Rapport non trouvé avec l'ID: " + id);
        }
        
        reportRepository.deleteById(id);
    }
    
    private void validateSqlQuery(String sqlQuery) {
        if (sqlQuery == null || sqlQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("La requête SQL ne peut pas être vide");
        }
        
        String upperQuery = sqlQuery.toUpperCase().trim();
        
        // Interdire les commandes dangereuses
        String[] forbiddenKeywords = {"DELETE", "DROP", "UPDATE", "INSERT", "CREATE", "ALTER", "TRUNCATE", "EXEC", "EXECUTE"};
        
        for (String keyword : forbiddenKeywords) {
            if (upperQuery.contains(keyword)) {
                throw new IllegalArgumentException("La requête SQL contient une commande interdite: " + keyword);
            }
        }
        
        // Vérifier que c'est un SELECT
        if (!upperQuery.startsWith("SELECT")) {
            throw new IllegalArgumentException("Seules les requêtes SELECT sont autorisées");
        }
    }
    
    private List<Map<String, Object>> executeQuery(String sqlQuery) {
        try {
            return jdbcTemplate.queryForList(sqlQuery);
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution de la requête SQL: {}", e.getMessage());
            throw new IllegalStateException("Erreur lors de l'exécution de la requête: " + e.getMessage(), e);
        }
    }
    
    private ReportResponse mapToResponse(Report report, List<Map<String, Object>> data) {
        return ReportResponse.builder()
                .id(report.getId())
                .name(report.getName())
                .description(report.getDescription())
                .sqlQuery(report.getSqlQuery())
                .chartType(report.getChartType())
                .xAxisColumn(report.getXAxisColumn())
                .yAxisColumn(report.getYAxisColumn())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .data(data)
                .build();
    }
}
