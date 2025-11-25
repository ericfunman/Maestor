package com.creditagricole.maestror.service;

import com.creditagricole.maestror.entity.*;
import com.creditagricole.maestror.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskCalculationService {

    private final OperationalRiskReferentialRepository riskReferentialRepository;
    private final IncidentRepository incidentRepository;
    private final ControlRepository controlRepository;
    private final RiskCalculationRepository riskCalculationRepository;

    /**
     * Scheduled task that runs daily at 2:00 AM
     * Calculates risk levels for all active risk referentials
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void calculateRiskLevels() {
        log.info("Starting scheduled risk calculation at {}", LocalDateTime.now());
        
        List<OperationalRiskReferential> activeRisks = riskReferentialRepository.findByActive(true);
        
        int processed = 0;
        for (OperationalRiskReferential risk : activeRisks) {
            try {
                calculateRiskForReferential(risk);
                processed++;
            } catch (Exception e) {
                log.error("Error calculating risk for referential {}: {}", risk.getRiskCode(), e.getMessage(), e);
            }
        }
        
        log.info("Risk calculation completed. Processed {} risks", processed);
    }

    /**
     * Calculate risk level for a specific risk referential
     */
    @Transactional
    public RiskCalculation calculateRiskForReferential(OperationalRiskReferential risk) {
        log.debug("Calculating risk for: {}", risk.getRiskCode());
        
        // Get incidents related to this risk
        List<Incident> incidents = incidentRepository.findByRiskReferentialId(risk.getId());
        
        // Get controls related to this risk
        List<Control> controls = controlRepository.findByRiskReferentialId(risk.getId());
        
        // Calculate metrics
        int incidentCount = incidents.size();
        int activeControlCount = (int) controls.stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .count();
        int effectiveControlCount = (int) controls.stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()) && "EFFECTIVE".equals(c.getEffectiveness()))
                .count();
        
        BigDecimal totalFinancialImpact = incidents.stream()
                .map(Incident::getFinancialImpact)
                .filter(impact -> impact != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate risk score (simple algorithm - can be enhanced)
        BigDecimal riskScore = calculateRiskScore(
                incidentCount,
                activeControlCount,
                effectiveControlCount,
                totalFinancialImpact,
                risk.getImpactLevel(),
                risk.getProbabilityLevel()
        );
        
        // Determine risk level based on score
        String riskLevel = determineRiskLevel(riskScore);
        
        // Create calculation record
        RiskCalculation calculation = RiskCalculation.builder()
                .riskReferential(risk)
                .entityCode(null) // Can be enhanced to calculate per entity
                .incidentCount(incidentCount)
                .activeControlCount(activeControlCount)
                .effectiveControlCount(effectiveControlCount)
                .totalFinancialImpact(totalFinancialImpact)
                .riskLevel(riskLevel)
                .riskScore(riskScore)
                .calculationDetails(buildCalculationDetails(incidentCount, activeControlCount, effectiveControlCount, totalFinancialImpact))
                .build();
        
        return riskCalculationRepository.save(calculation);
    }

    /**
     * Calculate risk score based on various factors
     * Algorithm can be customized based on business requirements
     */
    private BigDecimal calculateRiskScore(
            int incidentCount,
            int activeControlCount,
            int effectiveControlCount,
            BigDecimal financialImpact,
            String impactLevel,
            String probabilityLevel) {
        
        // Base score from incidents
        BigDecimal incidentScore = BigDecimal.valueOf(incidentCount * 10);
        
        // Impact level multiplier
        BigDecimal impactMultiplier = switch (impactLevel != null ? impactLevel : "MEDIUM") {
            case "HIGH" -> BigDecimal.valueOf(1.5);
            case "MEDIUM" -> BigDecimal.ONE;
            case "LOW" -> BigDecimal.valueOf(0.5);
            default -> BigDecimal.ONE;
        };
        
        // Probability level multiplier
        BigDecimal probabilityMultiplier = switch (probabilityLevel != null ? probabilityLevel : "MEDIUM") {
            case "HIGH" -> BigDecimal.valueOf(1.5);
            case "MEDIUM" -> BigDecimal.ONE;
            case "LOW" -> BigDecimal.valueOf(0.5);
            default -> BigDecimal.ONE;
        };
        
        // Control effectiveness reduction
        BigDecimal controlReduction = activeControlCount > 0
                ? BigDecimal.valueOf(effectiveControlCount).divide(BigDecimal.valueOf(activeControlCount), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        
        // Financial impact score (normalized)
        BigDecimal financialScore = financialImpact.divide(BigDecimal.valueOf(10000), 2, BigDecimal.ROUND_HALF_UP);
        
        // Final calculation
        BigDecimal score = incidentScore
                .multiply(impactMultiplier)
                .multiply(probabilityMultiplier)
                .add(financialScore)
                .multiply(BigDecimal.ONE.subtract(controlReduction.multiply(BigDecimal.valueOf(0.5))));
        
        return score.max(BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Determine risk level category based on calculated score
     */
    private String determineRiskLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "CRITICAL";
        } else if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "HIGH";
        } else if (score.compareTo(BigDecimal.valueOf(20)) >= 0) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Build detailed calculation explanation
     */
    private String buildCalculationDetails(
            int incidentCount,
            int activeControlCount,
            int effectiveControlCount,
            BigDecimal financialImpact) {
        
        return String.format(
                "Incidents: %d | Active Controls: %d | Effective Controls: %d | Financial Impact: %.2f EUR",
                incidentCount,
                activeControlCount,
                effectiveControlCount,
                financialImpact
        );
    }

    /**
     * Calculate risk for a specific entity
     */
    @Transactional
    public void calculateRiskForEntity(String entityCode) {
        log.info("Calculating risks for entity: {}", entityCode);
        
        List<OperationalRiskReferential> activeRisks = riskReferentialRepository.findByActive(true);
        
        for (OperationalRiskReferential risk : activeRisks) {
            calculateRiskForReferential(risk);
        }
    }
}
