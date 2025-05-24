package com.pharmacy.supplierinventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pharmacy.supplierinventory.entity.Inventory;

import java.util.List;

public interface InventoryRepository extends MongoRepository<Inventory, String> {
    List<Inventory> findBySupplierId(String supplierId);
}
