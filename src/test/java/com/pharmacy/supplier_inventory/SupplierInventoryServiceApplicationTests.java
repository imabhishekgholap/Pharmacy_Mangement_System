package com.pharmacy.supplier_inventory;

import com.pharmacy.supplierinventory.entity.Inventory;
import com.pharmacy.supplierinventory.entity.Supplier;
import com.pharmacy.supplierinventory.exception.ResourceNotFoundException;
import com.pharmacy.supplierinventory.repository.InventoryRepository;
import com.pharmacy.supplierinventory.repository.SupplierRepository;
import com.pharmacy.supplierinventory.service.impl.SupplierInventoryServiceImpl;
import com.pharmacy.supplierinventory.feign.DrugClient;
import com.pharmacy.supplierinventory.model.RestockDrugDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierInventoryServiceApplicationTests {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private DrugClient drugClient;

    @InjectMocks
    private SupplierInventoryServiceImpl supplierInventoryService;

    private Supplier supplier;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        supplier = new Supplier();
        supplier.setSupplierId("S001");
        supplier.setName("MediCorp");
        supplier.setContact("9876543210");
        supplier.setEmail("supplier@example.com");

        inventory = new Inventory();
        inventory.setInventoryId("I001");
        inventory.setDrugBatchId("B001");
        inventory.setSupplierId("S001");
        inventory.setExpiryDate(LocalDate.now().plusYears(1));
        inventory.setQuantity(100);
        inventory.setPrice(50.0);
    }

    // 1. Test for adding a supplier
    @Test
    void testAddSupplier() {
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        Supplier saved = supplierInventoryService.addSupplier(supplier);
        assertEquals(supplier.getSupplierId(), saved.getSupplierId());
    }

    // 2. Test for fetching inventories by supplier ID
    @Test
    void testGetInventoriesBySupplier() {
        when(inventoryRepository.findBySupplierId(inventory.getSupplierId())).thenReturn(List.of(inventory));
        List<Inventory> inventories = supplierInventoryService.getInventoriesBySupplier(inventory.getSupplierId());
        assertFalse(inventories.isEmpty());
    }

    // 3. Test for fetching inventory by ID
    @Test
    void testGetInventoryById() {
        when(inventoryRepository.findById(inventory.getInventoryId())).thenReturn(Optional.of(inventory));
        Inventory foundInventory = supplierInventoryService.getInventoryById(inventory.getInventoryId());
        assertEquals(inventory.getInventoryId(), foundInventory.getInventoryId());
    }

    // 4. Test for deleting a supplier
    @Test
    void testDeleteSupplier() {
        when(supplierRepository.findById(supplier.getSupplierId())).thenReturn(Optional.of(supplier));
        doNothing().when(supplierRepository).delete(supplier);

        Supplier deletedSupplier = supplierInventoryService.deleteSupplier(supplier);
        assertEquals(supplier.getSupplierId(), deletedSupplier.getSupplierId());
    }

    // 5. Test for updating a supplier
    @Test
    void testUpdateSupplier() {
        Supplier updatedSupplier = new Supplier();
        updatedSupplier.setSupplierId("S001");
        updatedSupplier.setName("Updated Corp");
        updatedSupplier.setContact("1234567890");
        updatedSupplier.setEmail("updated@example.com");

        when(supplierRepository.findById(supplier.getSupplierId())).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(updatedSupplier);

        Supplier result = supplierInventoryService.updateSupplier(updatedSupplier);
        assertEquals("Updated Corp", result.getName());
    }

    // 6. Test for restocking a drug in the pharmacy
    @Test
    void testRestockDrugInPharmacy() {
        RestockDrugDTO restockDrugDTO = new RestockDrugDTO(inventory.getDrugBatchId(), 50, LocalDate.of(2025, 12, 31), "supplier@example.com");

        doNothing().when(drugClient).restock(restockDrugDTO);

        assertDoesNotThrow(() -> supplierInventoryService.restockDrugInPharmacy(restockDrugDTO));
    }

}
