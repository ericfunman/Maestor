package com.creditagricole.maestror.controller;

import com.creditagricole.maestror.dto.FileUploadResponse;
import com.creditagricole.maestror.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @Test
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.csv",
            MediaType.TEXT_PLAIN_VALUE,
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

        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .param("category", "TEST")
                .param("uploadedBy", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileName").value("test.csv"))
            .andExpect(jsonPath("$.fileType").value("CSV"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testUploadFile_ValidationError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.csv",
            MediaType.TEXT_PLAIN_VALUE,
            "".getBytes()
        );

        when(fileUploadService.uploadFile(any(), eq("TEST"), eq("admin")))
            .thenThrow(new IllegalArgumentException("File is empty"));

        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .param("category", "TEST")
                .param("uploadedBy", "admin"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation error: File is empty"));
    }
}
