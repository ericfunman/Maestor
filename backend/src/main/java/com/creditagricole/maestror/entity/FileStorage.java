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
@Table(name = "file_storage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType; // CSV, EXCEL

    @Column(nullable = false)
    private String category; // REFERENTIAL, INCIDENT, CONTROL

    @Column(nullable = false)
    private Long fileSize;

    @Lob
    @Column(nullable = false)
    private byte[] fileContent;

    @Column(nullable = false)
    private String uploadedBy;

    @Column(nullable = false)
    private String status; // UPLOADED, PROCESSING, COMPLETED, FAILED

    @Column(length = 2000)
    private String errorMessage;

    @Column(nullable = false)
    private Integer totalRecords = 0;

    @Column(nullable = false)
    private Integer processedRecords = 0;

    @Column(nullable = false)
    private Integer failedRecords = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime processedAt;
}
