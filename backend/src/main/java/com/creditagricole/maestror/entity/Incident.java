package com.creditagricole.maestror.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String incidentCode;

    @Column(nullable = false)
    private String incidentTitle;

    @Column(length = 2000)
    private String incidentDescription;

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false)
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(nullable = false)
    private String status; // OPEN, IN_PROGRESS, CLOSED

    @Column
    private String entityCode; // Entity 1, 2, 3, etc.

    @Column
    private String businessUnit;

    @Column(precision = 15, scale = 2)
    private BigDecimal financialImpact;

    @Column
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_referential_id")
    private OperationalRiskReferential riskReferential;

    @Column
    private String detectedBy;

    @Column
    private LocalDate resolutionDate;

    @Column(length = 2000)
    private String correctiveActions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_storage_id")
    private FileStorage fileStorage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
