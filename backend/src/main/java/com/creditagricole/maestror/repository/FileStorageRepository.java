package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
    
    List<FileStorage> findByCategory(String category);
    
    List<FileStorage> findByStatus(String status);
    
    List<FileStorage> findByUploadedBy(String uploadedBy);
    
    List<FileStorage> findByUploadedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<FileStorage> findByCategoryAndStatus(String category, String status);
}
