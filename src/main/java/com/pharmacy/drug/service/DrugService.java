// DrugService.java
package com.pharmacy.drug.service;

import java.time.LocalDate;
import java.util.List;

import com.pharmacy.drug.entity.Drug;
import com.pharmacy.drug.model.RestockDrugDTO;

public interface DrugService {
    Drug createDrug(Drug drug);
    Drug getDrugByBatchId(String batchId);
    List<Drug> getAllDrugs();
    Drug updateDrug(String batchId, Drug drug);
    void deleteDrug(String batchId);
    List<Drug> getDrugsBySupplier(String supplierEmail);
    List<Drug> searchDrugsByName(String name);
    void checkDrugAvailability(String batchId, int quantity);
    void reduceDrugQuantity(String batchId, int quantity);
    void restock(RestockDrugDTO restocDrugDTO);
	List<Drug> getExpiredDrugsBeforeDate(LocalDate date);
}
