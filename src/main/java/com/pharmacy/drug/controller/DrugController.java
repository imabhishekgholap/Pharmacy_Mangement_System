//Drug Service
package com.pharmacy.drug.controller;

import com.pharmacy.drug.entity.Drug;
import com.pharmacy.drug.model.RestockDrugDTO;
import com.pharmacy.drug.service.DrugService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    private static final Logger logger = LoggerFactory.getLogger(DrugController.class);

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    // End-point to create a new drug
    @PostMapping
    public ResponseEntity<Drug> createDrug(@Valid @RequestBody Drug drug) {
        logger.info("Creating drug: {}", drug.getName());
        Drug createdDrug = drugService.createDrug(drug);
        return new ResponseEntity<>(createdDrug, HttpStatus.CREATED);
    }

    @PostMapping("/batch/{batchId}/check-availability")
    public ResponseEntity<String> checkDrugAvailability(@PathVariable String batchId, @RequestParam int quantity) {
        logger.info("Checking availability for batchId: {}, quantity: {}", batchId, quantity);
        drugService.checkDrugAvailability(batchId, quantity);
        return ResponseEntity.ok("Drug is available");
    }

    @PostMapping("/batch/{batchId}/reduce-quantity")
    public ResponseEntity<String> reduceDrugQuantity(@PathVariable String batchId, @RequestParam int quantity) {
        logger.info("Reducing quantity for batchId: {}, quantity: {}", batchId, quantity);
        drugService.reduceDrugQuantity(batchId, quantity);
        return ResponseEntity.ok("Drug quantity reduced successfully");
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<Drug> getDrugByBatchId(@PathVariable String batchId) {
        logger.info("Fetching drug by batchId: {}", batchId);
        Drug drug = drugService.getDrugByBatchId(batchId);
        return ResponseEntity.ok(drug);
    }

    @GetMapping("/view")
    public ResponseEntity<List<Drug>> getAllDrugs() {
        logger.info("Fetching all drugs");
        List<Drug> drugs = drugService.getAllDrugs();
        return ResponseEntity.ok(drugs);
    }

    @PutMapping("/batch/{batchId}")
    public ResponseEntity<Drug> updateDrug(@PathVariable String batchId, @Valid @RequestBody Drug drug) {
        logger.info("Updating drug with batchId: {}", batchId);
        Drug updatedDrug = drugService.updateDrug(batchId, drug);
        return ResponseEntity.ok(updatedDrug);
    }

    @DeleteMapping("/batch/{batchId}")
    public ResponseEntity<Map<String, String>> deleteDrug(@PathVariable String batchId) {
        logger.info("Deleting drug with batchId: {}", batchId);
        drugService.deleteDrug(batchId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Drug with batchId " + batchId + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/{supplierEmail}")
    public ResponseEntity<List<Drug>> getDrugsBySupplier(@PathVariable String supplierEmail) {
        logger.info("Fetching drugs for supplier email: {}", supplierEmail);
        List<Drug> drugs = drugService.getDrugsBySupplier(supplierEmail);
        return ResponseEntity.ok(drugs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Drug>> searchDrugsByName(@RequestParam String name) {
        logger.info("Searching drugs with name containing: {}", name);
        List<Drug> drugs = drugService.searchDrugsByName(name);
        return ResponseEntity.ok(drugs);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Drug>> getExpiredDrugs(@RequestParam(required = false) LocalDate expiryDate) {
        LocalDate targetDate = (expiryDate != null) ? expiryDate : LocalDate.now();
        logger.info("Fetching expired drugs before: {}", targetDate);
        List<Drug> drugs = drugService.getExpiredDrugsBeforeDate(targetDate);
        return ResponseEntity.ok(drugs);
    }

    @PostMapping("/restock")
    public ResponseEntity<String> restock(@RequestBody RestockDrugDTO restockDrugDTO) {
        logger.info("Restocking batchId: {} with quantity: {} and expiry date: {} by supplier {}",
                    restockDrugDTO.getBatchId(), restockDrugDTO.getQuantityToAdd(), 
                    restockDrugDTO.getExpiryDate(), restockDrugDTO.getEmail());

        drugService.restock(restockDrugDTO);

        return ResponseEntity.ok("Drug restocked successfully for batch ID: " + restockDrugDTO.getBatchId());
    }

}
