package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.dto.FileUploadResponse;
import com.creditagricole.maestror.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private FileUploadController fileUploadController;

    @Test
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.csv",
            "text/plain",
            "test,data\n1,2".getBytes()
        );

        FileUploadResponse response = FileUploadResponse.builder()
            .fileId(1L)
            .fileName("test.csv")
            .fileType("CSV")
            .category("TEST")
            .status("COMPLETED")
            .message("Upload successful")
            .build();

        when(fileUploadService.uploadFile(any(), eq("TEST"), eq("admin")))
            .thenReturn(response);

        ResponseEntity<FileUploadResponse> result = fileUploadController.uploadFile(file, "TEST", "admin");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("test.csv", result.getBody().getFileName());
        assertEquals("CSV", result.getBody().getFileType());
        assertEquals("COMPLETED", result.getBody().getStatus());
    }

    @Test
    void testUploadFile_ValidationError() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.csv",
            "text/plain",
            "".getBytes()
        );

        when(fileUploadService.uploadFile(any(), eq("TEST"), eq("admin")))
            .thenThrow(new IllegalArgumentException("File is empty"));

        ResponseEntity<FileUploadResponse> result = fileUploadController.uploadFile(file, "TEST", "admin");

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Validation error: File is empty", result.getBody().getMessage());
    }
}
