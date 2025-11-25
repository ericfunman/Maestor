package com.creditagricole.maestror.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    
    private Long fileId;
    private String fileName;
    private String fileType;
    private String category;
    private Long fileSize;
    private String status;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    private String message;
    private LocalDateTime uploadedAt;
}
