package com.creditagricole.maestror.service;

import com.creditagricole.maestror.entity.FileStorage;
import com.creditagricole.maestror.repository.FileStorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @Mock
    private FileStorageRepository fileStorageRepository;

    @Mock
    private FileProcessingService fileProcessingService;

    @InjectMocks
    private FileUploadService fileUploadService;

    @Mock
    private MultipartFile mockFile;

    @Test
    void testValidateFile_EmptyFile_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(true);
        when(mockFile.getOriginalFilename()).thenReturn("test.csv");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadFile(mockFile, "TEST", "testUser")
        );

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void testValidateFile_InvalidExtension_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getSize()).thenReturn(1024L);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadFile(mockFile, "TEST", "testUser")
        );

        assertEquals("Only CSV, Excel and TXT files are supported", exception.getMessage());
    }

    @Test
    void testValidateFile_FileTooLarge_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.csv");
        when(mockFile.getSize()).thenReturn(60L * 1024 * 1024); // 60MB

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadFile(mockFile, "TEST", "testUser")
        );

        assertEquals("File size exceeds maximum limit of 50MB", exception.getMessage());
    }

    @Test
    void testDetermineFileType_CSV() throws Exception {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.csv");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        FileStorage savedFileStorage = new FileStorage();
        savedFileStorage.setId(1L);
        savedFileStorage.setFileName("test.csv");
        savedFileStorage.setFileType("CSV");
        savedFileStorage.setStatus("UPLOADED");

        when(fileStorageRepository.save(any(FileStorage.class))).thenReturn(savedFileStorage);

        var response = fileUploadService.uploadFile(mockFile, "TEST", "testUser");

        assertEquals("CSV", response.getFileType());
        verify(fileStorageRepository, times(1)).save(any(FileStorage.class));
    }

    @Test
    void testDetermineFileType_Excel() throws Exception {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.xlsx");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        FileStorage savedFileStorage = new FileStorage();
        savedFileStorage.setId(1L);
        savedFileStorage.setFileName("test.xlsx");
        savedFileStorage.setFileType("EXCEL");
        savedFileStorage.setStatus("UPLOADED");

        when(fileStorageRepository.save(any(FileStorage.class))).thenReturn(savedFileStorage);

        var response = fileUploadService.uploadFile(mockFile, "TEST", "testUser");

        assertEquals("EXCEL", response.getFileType());
    }

    @Test
    void testDetermineFileType_TXT() throws Exception {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        FileStorage savedFileStorage = new FileStorage();
        savedFileStorage.setId(1L);
        savedFileStorage.setFileName("test.txt");
        savedFileStorage.setFileType("CSV");
        savedFileStorage.setStatus("UPLOADED");

        when(fileStorageRepository.save(any(FileStorage.class))).thenReturn(savedFileStorage);

        var response = fileUploadService.uploadFile(mockFile, "TEST", "testUser");

        assertEquals("CSV", response.getFileType());
    }
}
