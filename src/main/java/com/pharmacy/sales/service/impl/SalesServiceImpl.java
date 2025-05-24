package com.pharmacy.sales.service.impl;

import com.pharmacy.sales.entity.Sales;
import com.pharmacy.sales.exception.SalesServiceException;
import com.pharmacy.sales.model.SalesDTO;
import com.pharmacy.sales.repository.SalesRepository;
import com.pharmacy.sales.service.SalesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

@Service
public class SalesServiceImpl implements SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    @Autowired
    private SalesRepository salesRepository;

    /**
     * Records a sale by saving the provided SalesDTO details into the database.
     * Validates that the paid amount does not exceed the total price.
     *
     * @param request The sales details to be recorded
     * @return The saved Sales entity
     */
    @Override
    public Sales recordSale(SalesDTO request) {
        try {
            // Validation: paid amount should not exceed total price
            if (request.getTotalPrice() < request.getPaidAmount()) {
                logger.warn("Invalid payment: Paid amount ({}) exceeds total price ({})",
                        request.getPaidAmount(), request.getTotalPrice());
                throw new SalesServiceException("Paid amount cannot exceed total price.");
            }

            // Mapping DTO to entity
            Sales sales = new Sales();
            sales.setOrderId(request.getOrderId());
            sales.setBatchId(request.getBatchId());
            sales.setQuantity(request.getQuantity());
            sales.setTotalPrice(request.getTotalPrice());
            sales.setPaidAmount(request.getPaidAmount());
            sales.setDrugName(request.getDrugName());
            sales.setDoctorName(request.getDoctorName());

            // Calculate balance and set sale date
            double balance = request.getTotalPrice() - request.getPaidAmount();
            sales.setBalance(balance);
            sales.setSaleDate(LocalDate.now());

            logger.info("Recording sale for Order ID: {}, Paid Amount: {}, Balance: {}",
                    request.getOrderId(), request.getPaidAmount(), balance);

            // Save to database
            return salesRepository.save(sales);
        } catch (Exception e) {
            logger.error("Error recording sale: {}", e.getMessage(), e);
            throw new SalesServiceException("Error recording sale: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves sales records for a specific date.
     *
     * @param startDate The date for which to fetch sales
     * @return List of Sales for the given date
     */
    @Override
    public List<Sales> getSalesHistory(LocalDate startDate) {
        try {
            logger.info("Fetching sales history for date: {}", startDate);
            return salesRepository.findBySaleDate(startDate);
        } catch (Exception e) {
            logger.error("Error fetching sales history: {}", e.getMessage(), e);
            throw new SalesServiceException("Error fetching sales history: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all sales records from the database.
     *
     * @return List of all Sales
     */
    @Override
    public List<Sales> getAllSales() {
        try {
            logger.info("Fetching all sales records");
            return salesRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all sales: {}", e.getMessage(), e);
            throw new SalesServiceException("Error fetching all sales: " + e.getMessage(), e);
        }
    }
}
