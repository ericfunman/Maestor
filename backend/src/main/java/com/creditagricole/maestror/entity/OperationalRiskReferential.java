package com.creditagricole.maestror.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "operational_risk_referential")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationalRiskReferential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String riskCode;

    @Column(nullable = false)
    private String riskName;

    @Column(length = 2000)
    private String riskDescription;

    @Column(nullable = false)
    private String riskCategory;

    @Column(nullable = false)
    private String riskType;

    @Column
    private String businessLine;

    @Column
    private String impactLevel; // HIGH, MEDIUM, LOW

    @Column
    private String probabilityLevel; // HIGH, MEDIUM, LOW

    @Column
    private Boolean active = true;

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
