package com.pharmacy.drug.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pharmacy.drug.entity.Drug;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, String> {
    Optional<Drug> findByBatchIdIgnoreCase(String batchId);
    List<Drug> findBySupplierEmail(String supplierEmail);
    List<Drug> findByNameContainingIgnoreCase(String name);
    List<Drug> findByExpiryDateBefore(LocalDate date);
}
