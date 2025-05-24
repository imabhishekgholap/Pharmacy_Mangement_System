package com.pharmacy.supplierinventory.service.impl;

import com.pharmacy.supplierinventory.entity.Inventory;
import com.pharmacy.supplierinventory.entity.Supplier;
import com.pharmacy.supplierinventory.exception.ResourceNotFoundException;
import com.pharmacy.supplierinventory.feign.DrugClient;
import com.pharmacy.supplierinventory.model.RestockDrugDTO;
import com.pharmacy.supplierinventory.repository.InventoryRepository;
import com.pharmacy.supplierinventory.repository.SupplierRepository;
import com.pharmacy.supplierinventory.service.SupplierInventoryService;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierInventoryServiceImpl implements SupplierInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierInventoryServiceImpl.class);

    private final SupplierRepository supplierRepo;
    private final InventoryRepository inventoryRepo;
    private final DrugClient drugClient;

    public SupplierInventoryServiceImpl(SupplierRepository supplierRepo,
                                        InventoryRepository inventoryRepo,
                                        DrugClient drugClient) {
        this.supplierRepo = supplierRepo;
        this.inventoryRepo = inventoryRepo;
        this.drugClient = drugClient;
    }

    /**
     * Adds a new supplier to the system.
     *
     * @param supplier The supplier to be added
     * @return The saved supplier entity
     */
    @Override
    public Supplier addSupplier(Supplier supplier) {
        logger.info("Adding new supplier: {}", supplier.getName());
        return supplierRepo.save(supplier);
    }

    /**
     * Adds new inventory for a specific drug batch.
     * Verifies that the supplier exists and that the drug batch is valid.
     *
     * @param inventory The inventory to be added
     * @return The saved inventory entity
     * @throws ResourceNotFoundException If the supplier is not found
     * @throws RuntimeException If there is an error connecting to the drug-service
     */
    @Override
    public Inventory addInventory(Inventory inventory) {
        logger.info("Attempting to add inventory for batchId: {}", inventory.getDrugBatchId());

        // Verify supplier exists
        if (!supplierRepo.existsById(inventory.getSupplierId())) {
            logger.warn("Supplier not found with ID: {}", inventory.getSupplierId());
            throw new ResourceNotFoundException("Supplier not found with ID: " + inventory.getSupplierId());
        }

        // Verify drug batch exists in drug-service
        try {
            drugClient.getDrugByBatchId(inventory.getDrugBatchId());
        } catch (FeignException.NotFound e) {
            // Handle missing drug batch, proceed with inventory addition anyway
            logger.warn("Drug batch not found in drug-service for batchId: {}. Proceeding anyway.", inventory.getDrugBatchId());
        } catch (Exception e) {
            // Log and throw exception if there's an issue with drug service connection
            logger.error("Error connecting to drug-service while verifying batchId: {}", inventory.getDrugBatchId(), e);
            throw new RuntimeException("Error connecting to drug-service while verifying drug batch.");
        }

        return inventoryRepo.save(inventory);
    }

    /**
     * Retrieves all inventories for a given supplier.
     *
     * @param supplierId The ID of the supplier
     * @return A list of inventories associated with the supplier
     */
    @Override
    public List<Inventory> getInventoriesBySupplier(String supplierId) {
        logger.info("Fetching inventories for supplierId: {}", supplierId);
        return inventoryRepo.findBySupplierId(supplierId);
    }

    /**
     * Retrieves a specific inventory by its ID.
     *
     * @param id The ID of the inventory
     * @return The inventory object
     * @throws ResourceNotFoundException If the inventory is not found
     */
    @Override
    public Inventory getInventoryById(String id) {
        logger.info("Fetching inventory with ID: {}", id);
        return inventoryRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Inventory not found with ID: {}", id);
                    return new ResourceNotFoundException("Inventory not found with ID: " + id);
                });
    }

    /**
     * Deletes a supplier from the system. Ensures that the supplier exists before attempting deletion.
     *
     * @param supplier The supplier to be deleted
     * @return The deleted supplier entity
     * @throws ResourceNotFoundException If the supplier is not found
     */
    @Override
    public Supplier deleteSupplier(Supplier supplier) {
        logger.warn("Deleting supplier with ID: {}", supplier.getSupplierId());
        Supplier existing = supplierRepo.findById(supplier.getSupplierId())
                .orElseThrow(() -> {
                    logger.warn("Supplier not found with ID: {}", supplier.getSupplierId());
                    return new ResourceNotFoundException("Supplier not found with ID: " + supplier.getSupplierId());
                });

        supplierRepo.delete(existing);
        return existing;
    }

    /**
     * Retrieves all suppliers in the system.
     *
     * @return A list of all suppliers
     */
    @Override
    public List<Supplier> getAllSuppliers() {
        logger.info("Fetching all suppliers");
        return supplierRepo.findAll();
    }

    @Override
    public List<Inventory> getAllInventories() {
        logger.info("Fetching all inventories");
        return inventoryRepo.findAll();
    }

    /**
     * Updates an existing supplier's details.
     *
     * @param supplier The updated supplier entity
     * @return The updated supplier entity
     * @throws ResourceNotFoundException If the supplier is not found
     */
    @Override
    public Supplier updateSupplier(Supplier supplier) {
        logger.info("Updating supplier with ID: {}", supplier.getSupplierId());
        
        Supplier existing = supplierRepo.findById(supplier.getSupplierId())
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplier.getSupplierId()));
        
//        for update logic we not validating entity validations ,admin just want to change specific attribute for reason
        if (supplier.getName() != null) {
            existing.setName(supplier.getName());
        }
        if (supplier.getContact() != null) {
            existing.setContact(supplier.getContact());
        }
        if (supplier.getEmail() != null) {
            existing.setEmail(supplier.getEmail());
        }

        return supplierRepo.save(existing);
    }
   

    /**
     * Sends a request to restock a drug in the pharmacy system for a specific batch.
     * Verifies the drug's existence and attempts to restock.
     *
     * @param batchId The batch ID of the drug to be restocked
     * @param quantityToAdd The quantity of the drug to add to the stock
     * @throws ResourceNotFoundException If the drug batch is not found
     * @throws RuntimeException If there is an error during the restocking process
     */
    @Override
    public void restockDrugInPharmacy(RestockDrugDTO restockDrugDTO) {
        logger.info("Sending restock request for batchId: {} with quantity: {} and expiry date: {}", 
                    restockDrugDTO.getBatchId(), restockDrugDTO.getQuantityToAdd(), restockDrugDTO.getExpiryDate());
        try {
            drugClient.restock(restockDrugDTO);
        } catch (FeignException.NotFound e) {
            // Handle the case where the drug batch does not exist
            logger.error("Drug not found for restocking with batchId: {}", restockDrugDTO.getBatchId());
            throw new ResourceNotFoundException("Drug not found for restocking with batchId: " + restockDrugDTO.getBatchId());
        } catch (Exception e) {
            // Log and throw exception if there’s a failure in the restocking process
            logger.error("Failed to restock drug with batchId: {}", restockDrugDTO.getBatchId(), e);
            throw new RuntimeException("Failed to restock drug: " + e.getMessage());
        }
    }

}
