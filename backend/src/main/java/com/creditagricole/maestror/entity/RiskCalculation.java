package com.creditagricole.maestror.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_calculation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_referential_id", nullable = false)
    private OperationalRiskReferential riskReferential;

    @Column
    private String entityCode;

    @Column(nullable = false)
    private Integer incidentCount;

    @Column(nullable = false)
    private Integer activeControlCount;

    @Column(nullable = false)
    private Integer effectiveControlCount;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalFinancialImpact;

    @Column(nullable = false)
    private String riskLevel; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(length = 2000)
    private String calculationDetails;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime calculatedAt;
}
