package com.creditagricole.maestror.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "control")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Control {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String controlCode;

    @Column(nullable = false)
    private String controlName;

    @Column(length = 2000)
    private String controlDescription;

    @Column(nullable = false)
    private String controlType; // PREVENTIVE, DETECTIVE, CORRECTIVE

    @Column(nullable = false)
    private String frequency; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY

    @Column
    private String entityCode; // Entity 1, 2, 3, etc.

    @Column
    private String responsiblePerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_referential_id")
    private OperationalRiskReferential riskReferential;

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE

    @Column
    private LocalDate lastExecutionDate;

    @Column
    private String lastExecutionResult; // PASSED, FAILED, PARTIAL

    @Column(length = 2000)
    private String comments;

    @Column
    private String effectiveness; // EFFECTIVE, PARTIALLY_EFFECTIVE, INEFFECTIVE

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
