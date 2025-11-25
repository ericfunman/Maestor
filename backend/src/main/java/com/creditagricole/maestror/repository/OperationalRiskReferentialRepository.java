package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.OperationalRiskReferential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationalRiskReferentialRepository extends JpaRepository<OperationalRiskReferential, Long> {
    
    Optional<OperationalRiskReferential> findByRiskCode(String riskCode);
    
    List<OperationalRiskReferential> findByRiskCategory(String riskCategory);
    
    List<OperationalRiskReferential> findByActive(Boolean active);
    
    List<OperationalRiskReferential> findByBusinessLine(String businessLine);
}
