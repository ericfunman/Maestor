package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.dto.FileUploadResponse;
import com.creditagricole.maestror.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload", description = "API for uploading and managing files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file", description = "Upload CSV or Excel file for processing")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam(value = "uploadedBy", defaultValue = "system") String uploadedBy) {
        
        log.info("Received upload request - File: {}, Category: {}, UploadedBy: {}", 
                file.getOriginalFilename(), category, uploadedBy);
        
        try {
            FileUploadResponse response = fileUploadService.uploadFile(file, category, uploadedBy);
            log.info("Upload successful - FileId: {}, Status: {}", response.getFileId(), response.getStatus());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Validation error during upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    FileUploadResponse.builder()
                            .message("Validation error: " + e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Upload failed with exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    FileUploadResponse.builder()
                            .message("Upload failed: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{fileId}/status")
    @Operation(summary = "Get file processing status", description = "Retrieve the processing status of an uploaded file")
    public ResponseEntity<FileUploadResponse> getFileStatus(@PathVariable Long fileId) {
        try {
            FileUploadResponse response = fileUploadService.getFileStatus(fileId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
