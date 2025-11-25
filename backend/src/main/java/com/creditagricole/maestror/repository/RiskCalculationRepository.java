package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.RiskCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskCalculationRepository extends JpaRepository<RiskCalculation, Long> {
    
    List<RiskCalculation> findByEntityCode(String entityCode);
    
    List<RiskCalculation> findByRiskLevel(String riskLevel);
    
    List<RiskCalculation> findByCalculatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT rc FROM RiskCalculation rc WHERE rc.riskReferential.id = :riskId ORDER BY rc.calculatedAt DESC")
    List<RiskCalculation> findLatestByRiskReferentialId(Long riskId);
    
    @Query("SELECT rc FROM RiskCalculation rc WHERE rc.entityCode = :entityCode ORDER BY rc.riskScore DESC")
    List<RiskCalculation> findTopRisksByEntity(String entityCode);
}
