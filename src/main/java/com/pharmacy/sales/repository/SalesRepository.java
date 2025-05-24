package com.pharmacy.sales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pharmacy.sales.entity.Sales;

import feign.Param;

import java.util.List;
import java.time.LocalDate;
import java.util.Date;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    List<Sales> findBySaleDate(LocalDate saleDate); 
}