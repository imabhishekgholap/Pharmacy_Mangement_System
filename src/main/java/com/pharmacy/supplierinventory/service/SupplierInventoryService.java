// SupplierInventoryService.java
package com.pharmacy.supplierinventory.service;

import java.util.List;

import com.pharmacy.supplierinventory.entity.Inventory;
import com.pharmacy.supplierinventory.entity.Supplier;
import com.pharmacy.supplierinventory.model.RestockDrugDTO;

public interface SupplierInventoryService {
    Supplier addSupplier(Supplier supplier);
    Inventory addInventory(Inventory inventory);
    List<Inventory> getInventoriesBySupplier(String supplierId);
    Inventory getInventoryById(String id);
    List<Inventory> getAllInventories();
    List<Supplier> getAllSuppliers();
    void restockDrugInPharmacy(RestockDrugDTO restockDrugDTO);
    Supplier updateSupplier(Supplier supplier);
    Supplier deleteSupplier(Supplier supplier);
}
