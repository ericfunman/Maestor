package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.Control;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ControlRepository extends JpaRepository<Control, Long> {
    
    Optional<Control> findByControlCode(String controlCode);
    
    List<Control> findByStatus(String status);
    
    List<Control> findByEntityCode(String entityCode);
    
    List<Control> findByControlType(String controlType);
    
    List<Control> findByEffectiveness(String effectiveness);
    
    @Query("SELECT c FROM Control c WHERE c.riskReferential.id = :riskId")
    List<Control> findByRiskReferentialId(Long riskId);
    
    @Query("SELECT COUNT(c) FROM Control c WHERE c.entityCode = :entityCode AND c.status = 'ACTIVE' AND c.effectiveness = :effectiveness")
    Integer countActiveAndEffectiveControls(String entityCode, String effectiveness);
}
