package com.pharmacy.supplierinventory.controller;

import com.pharmacy.supplierinventory.entity.Inventory;
import com.pharmacy.supplierinventory.entity.Supplier;
import com.pharmacy.supplierinventory.model.RestockDrugDTO;
import com.pharmacy.supplierinventory.service.SupplierInventoryService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplierinventory")
@Validated
public class SupplierInventoryController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierInventoryController.class);

    private final SupplierInventoryService service;

    public SupplierInventoryController(SupplierInventoryService service) {
        this.service = service;
    }

    @PostMapping("/supplier")
    public Supplier addSupplier(@Valid @RequestBody Supplier supplier) {
        return service.addSupplier(supplier);
    }

    @PostMapping("/inventory")
    public Inventory addInventory(@Valid @RequestBody Inventory inventory) {
        return service.addInventory(inventory);
    }

    @GetMapping("/supplier/{supplierId}/inventories")
    public List<Inventory> getInventoriesBySupplier(@PathVariable String supplierId) {
        logger.info("Fetching inventories for supplierId: {}", supplierId);
        return service.getInventoriesBySupplier(supplierId);
    }

    @GetMapping("/inventory/{id}")
    public Inventory getInventory(@PathVariable String id) {
        logger.info("Fetching inventory with ID: {}", id);
        return service.getInventoryById(id);
    }

    @GetMapping("/suppliers")
    public List<Supplier> getAllSuppliers() {
        logger.info("Fetching all suppliers");
        return service.getAllSuppliers();
    }

    @DeleteMapping("/suppliers")
    public ResponseEntity<String> deleteSupplier(@RequestBody Supplier supplier) {
        logger.warn("Deleting supplier: {}", supplier.getSupplierId());
        Supplier deleted = service.deleteSupplier(supplier);
        return ResponseEntity.ok("Supplier with " + deleted.getSupplierId()+ " is deleted successfully!");
    }
    
    @GetMapping("/inventories")
    public List<Inventory> getAllInventories() {
        logger.info("Fetching all inventories");
        return service.getAllInventories();
    }

    @PutMapping("/suppliers/{supplierId}")
    public ResponseEntity<Supplier> updateSupplier(
            @Valid @PathVariable String supplierId,
            @RequestBody Supplier supplier) {

        supplier.setSupplierId(supplierId);
        logger.info("Updating supplier with ID: {}", supplierId);
        Supplier updated = service.updateSupplier(supplier);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/restock")
    public ResponseEntity<String> restockDrug(@RequestBody RestockDrugDTO restockDrugDTO) {
        logger.info("Restocking drug in pharmacy - Batch ID: {}, Quantity: {}, Expiry Date: {}, Supplier: {}", 
                    restockDrugDTO.getBatchId(), restockDrugDTO.getQuantityToAdd(), 
                    restockDrugDTO.getExpiryDate(), restockDrugDTO.getEmail());

        service.restockDrugInPharmacy(restockDrugDTO);

        return ResponseEntity.ok("Restocked Batch ID " + restockDrugDTO.getBatchId() + 
                                 " with quantity " + restockDrugDTO.getQuantityToAdd() + 
                                 " and expiry date " + restockDrugDTO.getExpiryDate());
    }

}
