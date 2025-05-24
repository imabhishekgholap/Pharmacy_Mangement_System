package com.pharmacy.sales;

import com.pharmacy.sales.entity.Sales;
import com.pharmacy.sales.exception.SalesServiceException;
import com.pharmacy.sales.model.SalesDTO;
import com.pharmacy.sales.repository.SalesRepository;
import com.pharmacy.sales.service.impl.SalesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesServiceApplicationTests {

    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesServiceImpl salesService;

    private Sales sales;
    private SalesDTO salesDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sales = new Sales();
        sales.setOrderId("O001");
        sales.setBatchId("B001");
        sales.setQuantity(5);
        sales.setTotalPrice(100.0);
        sales.setPaidAmount(50.0);
        sales.setBalance(50.0);
        sales.setDrugName("Paracetamol");
        sales.setDoctorName("Dr. Smith");
        sales.setSaleDate(LocalDate.now());

        salesDTO = new SalesDTO("O001", "B001", 5, 100.0, 50.0, "Dr. Smith", "Paracetamol");
    }

    // 1. Test for recording a sale
    @Test
    void testRecordSale() {
        when(salesRepository.save(any(Sales.class))).thenReturn(sales);
        Sales saved = salesService.recordSale(salesDTO);
        assertEquals(sales.getOrderId(), saved.getOrderId());
        assertEquals(sales.getBalance(), saved.getBalance());
    }

    // 2. Test for invalid paid amount
    @Test
    void testRecordSaleInvalidPaidAmount() {
        salesDTO.setPaidAmount(150.0); // Paid amount exceeds total price
        assertThrows(SalesServiceException.class, () -> salesService.recordSale(salesDTO));
    }

    // 3. Test for fetching sales history by date
    @Test
    void testGetSalesHistory() {
        when(salesRepository.findBySaleDate(any(LocalDate.class))).thenReturn(List.of(sales));
        List<Sales> result = salesService.getSalesHistory(LocalDate.now());
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // 4. Test for fetching all sales records
    @Test
    void testGetAllSales() {
        when(salesRepository.findAll()).thenReturn(List.of(sales));
        List<Sales> result = salesService.getAllSales();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
