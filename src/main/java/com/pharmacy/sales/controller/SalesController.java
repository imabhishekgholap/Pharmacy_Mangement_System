package com.pharmacy.sales.controller;

import com.pharmacy.sales.entity.Sales;
import com.pharmacy.sales.model.SalesDTO;
import com.pharmacy.sales.repository.SalesRepository;
import com.pharmacy.sales.service.SalesReportService;
import com.pharmacy.sales.service.SalesService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sales")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    @Autowired
    private SalesService salesService;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private SalesReportService salesReportService;

    @PostMapping("/record")
    public ResponseEntity<Sales> recordSale(@Valid @RequestBody SalesDTO salesDTO) {
        logger.info("Received request to record sale for orderId: {}", salesDTO.getOrderId());
        Sales savedSale = salesService.recordSale(salesDTO);
        logger.info("Sale recorded successfully for orderId: {}", savedSale.getOrderId());
        return ResponseEntity.ok(savedSale);
    }

    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadSalesReport() {
        logger.info("Request received to download sales report");
        try {
            byte[] pdfBytes = salesReportService.generateSalesReport();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=sales_report.pdf");

            logger.info("Sales report generated successfully");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            logger.error("Error generating sales report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Sales>> getSalesHistory(
            @RequestParam("startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        logger.info("Fetching sales history for date: {}", startDate);
        List<Sales> salesHistory = salesRepository.findBySaleDate(startDate);

        if (salesHistory.isEmpty()) {
            logger.warn("No sales history found for date: {}", startDate);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Returning {} sales records for date: {}", salesHistory.size(), startDate);
        return ResponseEntity.ok(salesHistory);
    }

    @GetMapping("/getAll")
    public List<Sales> getAllSalesReport() {
        logger.info("Fetching all sales records");
        List<Sales> allSales = salesService.getAllSales();
        logger.info("Found {} total sales records", allSales.size());
        return allSales;
    }
}
