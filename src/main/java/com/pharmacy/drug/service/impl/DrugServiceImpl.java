//DrugServiceImpl
package com.pharmacy.drug.service.impl;

import com.pharmacy.drug.entity.Drug;
import com.pharmacy.drug.exception.DrugNotFoundException;
import com.pharmacy.drug.exception.DrugUnavailableException;
import com.pharmacy.drug.exception.InvalidRestockRequestException;
import com.pharmacy.drug.exception.InvalidSupplierException;
import com.pharmacy.drug.model.RestockDrugDTO;
import com.pharmacy.drug.repository.DrugRepository;
import com.pharmacy.drug.service.DrugService;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DrugServiceImpl implements DrugService {

    private static final Logger logger = LoggerFactory.getLogger(DrugServiceImpl.class);
    private final DrugRepository drugRepository;

    public DrugServiceImpl(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Override
    public Drug createDrug(Drug drug) {
        if (drug == null) {
            logger.error("Attempted to create a null drug.");
            throw new IllegalArgumentException("Drug cannot be null");
        }

        Optional<Drug> existingDrug = drugRepository.findByBatchIdIgnoreCase(drug.getBatchId());
        if (existingDrug.isPresent()) {
            logger.warn("Drug with batchId {} already exists.", drug.getBatchId());
            throw new IllegalStateException("Drug with this batch ID already exists");
        }

        logger.info("Creating new drug with batchId: {}", drug.getBatchId());
        return drugRepository.save(drug);
    }

    @Override
    public Drug getDrugByBatchId(String batchId) {
        logger.info("Looking up drug by batchId: {}", batchId);

        Optional<Drug> optionalDrug = drugRepository.findByBatchIdIgnoreCase(batchId);
        if (optionalDrug.isEmpty()) {
            logger.warn("No drug found with batchId: {}", batchId);
            throw new DrugNotFoundException("Drug not found with batchId: " + batchId);
        }

        logger.info("Drug found: {}", optionalDrug.get());
        return optionalDrug.get();
    }

    @Override
    public List<Drug> getAllDrugs() {
        logger.info("Fetching all drugs");

        List<Drug> drugs = drugRepository.findAll();
        if (drugs.isEmpty()) {
            logger.warn("No drugs found in the system");
            throw new DrugNotFoundException("No drugs found in the system");
        }
        return drugs;
    }

    @Override
    public Drug updateDrug(String batchId, Drug drug) {
        logger.info("Updating drug with batchId: {}", batchId);

        if (batchId == null || batchId.trim().isEmpty()) {
            logger.error("Batch ID is null or empty");
            throw new IllegalArgumentException("Batch ID cannot be null or empty");
        }

        if (drug == null) {
            logger.error("Drug object is null");
            throw new IllegalArgumentException("Drug object cannot be null");
        }

        // Ensure the drug exists before updating
        getDrugByBatchId(batchId);
        return drugRepository.save(drug);
    }

    @Override
    public void deleteDrug(String batchId) {
        logger.info("Deleting drug with batchId: {}", batchId);

        // Retrieve drug before deleting to ensure it exists
        Drug drug = getDrugByBatchId(batchId);
        drugRepository.delete(drug);

        logger.info("Successfully deleted drug with batchId: {}", batchId);
    }

    @Override
    public List<Drug> getDrugsBySupplier(String supplierEmail) {
        logger.info("Fetching drugs for supplier email: {}", supplierEmail);

        List<Drug> drugs = drugRepository.findBySupplierEmail(supplierEmail);
        if (drugs.isEmpty()) {
            logger.warn("No drugs found for supplier email: {}", supplierEmail);
            throw new DrugNotFoundException("No drugs found for supplier email: " + supplierEmail);
        }
        return drugs;
    }

    @Override
    public List<Drug> searchDrugsByName(String name) {
        logger.info("Searching drugs by name: {}", name);

        List<Drug> drugs = drugRepository.findByNameContainingIgnoreCase(name);
        if (drugs.isEmpty()) {
            logger.warn("No drugs found matching name: {}", name);
            throw new DrugNotFoundException("No drugs found matching name: " + name);
        }
        return drugs;
    }

    @Override
    public List<Drug> getExpiredDrugsBeforeDate(LocalDate date) {
        logger.info("Fetching expired drugs before {}", date);

        List<Drug> drugs = drugRepository.findByExpiryDateBefore(date);
        if (drugs.isEmpty()) {
            logger.warn("No expired drugs found before {}", date);
            throw new DrugNotFoundException("No expired drugs found before " + date);
        }
        return drugs;
    }

    @Override
    public void checkDrugAvailability(String batchId, int quantity) {
        logger.info("Checking availability for drug batchId: {} with quantity: {}", batchId, quantity);

        Drug drug = drugRepository.findByBatchIdIgnoreCase(batchId)
                .orElseThrow(() -> {
                    logger.warn("Drug not found with batchId: {}", batchId);
                    return new DrugNotFoundException("Drug not found with batchId: " + batchId);
                });

        if (drug.getQuantity() < quantity) {
            logger.warn("Insufficient stock for drug: {} (Available: {}, Requested: {})",
                        drug.getName(), drug.getQuantity(), quantity);
            throw new DrugUnavailableException("Not enough stock for drug: " + drug.getName());
        }
    }

    @Override
    @Transactional
    public void reduceDrugQuantity(String batchId, int quantity) {
        logger.info("Reducing quantity for drug batchId: {} by {}", batchId, quantity);

        Drug drug = drugRepository.findByBatchIdIgnoreCase(batchId)
                .orElseThrow(() -> {
                    logger.warn("Drug not found for quantity reduction, batchId: {}", batchId);
                    return new DrugNotFoundException("Drug not found with batchId: " + batchId);
                });

        if (drug.getQuantity() < quantity) {
            logger.warn("Attempted to reduce more than available stock for drug: {}", drug.getName());
            throw new DrugUnavailableException("Not enough stock to reduce for drug: " + drug.getName());
        }

        drug.setQuantity(drug.getQuantity() - quantity);
        drugRepository.save(drug);
        logger.info("Reduced quantity successfully for drug batchId: {}", batchId);
    }

    @Override
    public void restock(RestockDrugDTO restockDrugDTO) {
        logger.info("Received restocking request: {}", restockDrugDTO);

        try {
            Drug drug = drugRepository.findByBatchIdIgnoreCase(restockDrugDTO.getBatchId())
                    .orElseThrow(() -> {
                        logger.warn("Drug not found for batch ID: {}", restockDrugDTO.getBatchId());
                        return new DrugNotFoundException("Drug not found with batchId: " + restockDrugDTO.getBatchId());
                    });

            // Validate supplier assignment
            if (drug.getSupplierEmail() == null || !drug.getSupplierEmail().equals(restockDrugDTO.getEmail())) {
                logger.warn("Supplier mismatch or not assigned for batch ID: {}", restockDrugDTO.getBatchId());
                throw new InvalidSupplierException("Supplier mismatch or not assigned for batch ID: " + restockDrugDTO.getBatchId());
            }

            // Ensure expiry date is valid
            if (restockDrugDTO.getExpiryDate() == null) {
                logger.warn("Expiry date missing for batch ID: {}", restockDrugDTO.getBatchId());
                throw new InvalidRestockRequestException("Expiry date cannot be null for restocking.");
            }

            // Update quantity and expiry date
            drug.setQuantity(drug.getQuantity() + restockDrugDTO.getQuantityToAdd());
            drug.setExpiryDate(restockDrugDTO.getExpiryDate());

            drugRepository.save(drug);

            logger.info("Successfully restocked drug batch ID: {}. New quantity: {}. Expiry date: {}", 
                        restockDrugDTO.getBatchId(), drug.getQuantity(), restockDrugDTO.getExpiryDate());
        } catch (Exception e) {
            logger.error("Unexpected error while restocking drug batch ID: {}", restockDrugDTO.getBatchId(), e);
            throw new RuntimeException("Failed to restock drug: " + e.getMessage());
        }
    }


}
