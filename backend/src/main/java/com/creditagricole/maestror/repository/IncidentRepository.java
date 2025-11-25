package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    
    Optional<Incident> findByIncidentCode(String incidentCode);
    
    List<Incident> findByStatus(String status);
    
    List<Incident> findByEntityCode(String entityCode);
    
    List<Incident> findBySeverity(String severity);
    
    List<Incident> findByIncidentDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT i FROM Incident i WHERE i.riskReferential.id = :riskId")
    List<Incident> findByRiskReferentialId(Long riskId);
    
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.entityCode = :entityCode AND i.status = :status")
    Integer countByEntityCodeAndStatus(String entityCode, String status);
}
