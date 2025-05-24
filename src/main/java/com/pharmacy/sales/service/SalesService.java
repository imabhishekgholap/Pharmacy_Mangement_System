package com.pharmacy.sales.service;

import com.pharmacy.sales.entity.Sales;
import com.pharmacy.sales.model.SalesDTO;

import java.time.LocalDate;
import java.util.List;

public interface SalesService{
    Sales recordSale(SalesDTO request); // Records a sale
    List<Sales> getSalesHistory(LocalDate startDate); // Fetch sales by date
    List<Sales> getAllSales(); // Fetch all sales
}