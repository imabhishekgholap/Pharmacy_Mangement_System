package com.pharmacy.drug;

import com.pharmacy.drug.entity.Drug;
import com.pharmacy.drug.exception.DrugNotFoundException;
import com.pharmacy.drug.exception.DrugUnavailableException;
import com.pharmacy.drug.model.RestockDrugDTO;
import com.pharmacy.drug.repository.DrugRepository;
import com.pharmacy.drug.service.impl.DrugServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrugServiceApplicationTests {

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private DrugServiceImpl drugService;

    private Drug drug;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        drug = new Drug();
        drug.setBatchId("B001");
        drug.setName("Paracetamol");
        drug.setQuantity(50);
        drug.setSupplierEmail("supplier@example.com");
        drug.setExpiryDate(LocalDate.of(2025, 5, 1));
    }

    // 1. Test for creating a drug
    @Test
    void testCreateDrug() {
        when(drugRepository.findByBatchIdIgnoreCase(drug.getBatchId())).thenReturn(Optional.empty());
        when(drugRepository.save(drug)).thenReturn(drug);
        Drug saved = drugService.createDrug(drug);
        assertEquals(drug.getBatchId(), saved.getBatchId());
    }

    // 2. Test for fetching a drug by batch ID
    @Test
    void testGetDrugByBatchId() {
        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        Drug found = drugService.getDrugByBatchId("B001");
        assertEquals("Paracetamol", found.getName());
    }

    // 3. Test for fetching all drugs
    @Test
    void testGetAllDrugs() {
        when(drugRepository.findAll()).thenReturn(Arrays.asList(drug));
        List<Drug> drugs = drugService.getAllDrugs();
        assertFalse(drugs.isEmpty());
    }

    // 4. Test for updating a drug
    @Test
    void testUpdateDrug() {
        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        when(drugRepository.save(drug)).thenReturn(drug);
        Drug updated = drugService.updateDrug("B001", drug);
        assertEquals("B001", updated.getBatchId());
    }

    // 5. Test for deleting a drug
    @Test
    void testDeleteDrug() {
        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        doNothing().when(drugRepository).delete(drug);
        assertDoesNotThrow(() -> drugService.deleteDrug("B001"));
    }

    // 6. Test for checking drug availability (success scenario)
    @Test
    void testCheckDrugAvailabilitySuccess() {
        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        assertDoesNotThrow(() -> drugService.checkDrugAvailability("B001", 20));
    }

    // 7. Test for checking drug availability (failure scenario)
    @Test
    void testCheckDrugAvailabilityFail() {
        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        assertThrows(DrugUnavailableException.class, () -> drugService.checkDrugAvailability("B001", 100));
    }

    // 8. Test for reducing drug quantity
    @Test
    void testReduceDrugQuantity() {
        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        when(drugRepository.save(any())).thenReturn(drug);
        drugService.reduceDrugQuantity("B001", 10);
        verify(drugRepository).save(any());
    }

    // 9. Test for restocking drugs
    @Test
    void testRestock() {
        RestockDrugDTO restockDrugDTO = new RestockDrugDTO("B001", 30, LocalDate.of(2025, 12, 31), "supplier@example.com");

        when(drugRepository.findByBatchIdIgnoreCase("B001")).thenReturn(Optional.of(drug));
        when(drugRepository.save(any())).thenReturn(drug);

        assertDoesNotThrow(() -> drugService.restock(restockDrugDTO));

        verify(drugRepository).save(any());
    }

    // 10. Test for fetching drugs by supplier email
    @Test
    void testGetDrugsBySupplier() {
        when(drugRepository.findBySupplierEmail("supplier@example.com")).thenReturn(List.of(drug));
        List<Drug> result = drugService.getDrugsBySupplier("supplier@example.com");
        assertEquals(1, result.size());
    }

    // 11. Test for searching drugs by name
    @Test
    void testSearchDrugsByName() {
        when(drugRepository.findByNameContainingIgnoreCase("Para")).thenReturn(List.of(drug));
        List<Drug> result = drugService.searchDrugsByName("Para");
        assertEquals(1, result.size());
    }

    // 12. Test for retrieving expired drugs before a certain date
    @Test
    void testGetExpiredDrugsBeforeDate() {
        when(drugRepository.findByExpiryDateBefore(LocalDate.of(2026, 1, 1))).thenReturn(List.of(drug));
        List<Drug> result = drugService.getExpiredDrugsBeforeDate(LocalDate.of(2026, 1, 1));
        assertFalse(result.isEmpty());
    }
}
