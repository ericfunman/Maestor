package com.creditagricole.maestror.service;

import com.creditagricole.maestror.dto.FileUploadResponse;
import com.creditagricole.maestror.entity.*;
import com.creditagricole.maestror.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final FileStorageRepository fileStorageRepository;
    private final FileProcessingService fileProcessingService;

    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, String category, String uploadedBy) {
        try {
            // Validate file
            validateFile(file);
            
            // Determine file type
            String fileType = determineFileType(file.getOriginalFilename());
            
            // Save file to database
            FileStorage fileStorage = FileStorage.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(fileType)
                    .category(category.toUpperCase())
                    .fileSize(file.getSize())
                    .fileContent(file.getBytes())
                    .uploadedBy(uploadedBy)
                    .status("UPLOADED")
                    .totalRecords(0)
                    .processedRecords(0)
                    .failedRecords(0)
                    .build();
            
            fileStorage = fileStorageRepository.save(fileStorage);
            log.info("File uploaded successfully: {} (ID: {})", file.getOriginalFilename(), fileStorage.getId());
            
            // Issue #9 - Extraire le traitement dans une methode separee
            processFileWithErrorHandling(fileStorage);
            
            return buildResponse(fileStorage, "File uploaded successfully");
            
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    private void processFileWithErrorHandling(FileStorage fileStorage) {
        try {
            fileProcessingService.processFile(fileStorage);
        } catch (Exception e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            fileStorage.setStatus("FAILED");
            fileStorage.setErrorMessage(e.getMessage());
            fileStorageRepository.save(fileStorage);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        
        String extension = getFileExtension(filename);
        if (!extension.equals("csv") && !extension.equals("xlsx") && !extension.equals("xls") && !extension.equals("txt")) {
            throw new IllegalArgumentException("Only CSV, Excel and TXT files are supported");
        }
        
        // Max 50MB
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 50MB");
        }
    }

    private String determineFileType(String filename) {
        String extension = getFileExtension(filename);
        return switch (extension.toLowerCase()) {
            case "csv", "txt" -> "CSV";
            case "xlsx", "xls" -> "EXCEL";
            default -> "UNKNOWN";
        };
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private FileUploadResponse buildResponse(FileStorage fileStorage, String message) {
        return FileUploadResponse.builder()
                .fileId(fileStorage.getId())
                .fileName(fileStorage.getFileName())
                .fileType(fileStorage.getFileType())
                .category(fileStorage.getCategory())
                .fileSize(fileStorage.getFileSize())
                .status(fileStorage.getStatus())
                .totalRecords(fileStorage.getTotalRecords())
                .processedRecords(fileStorage.getProcessedRecords())
                .failedRecords(fileStorage.getFailedRecords())
                .message(message)
                .uploadedAt(fileStorage.getUploadedAt())
                .build();
    }

    public FileUploadResponse getFileStatus(Long fileId) {
        FileStorage fileStorage = fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));
        
        return buildResponse(fileStorage, "File status retrieved successfully");
    }
}
