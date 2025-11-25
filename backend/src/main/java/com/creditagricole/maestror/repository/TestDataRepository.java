package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.TestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {
    
    List<TestData> findByFileStorageId(Long fileStorageId);
}
