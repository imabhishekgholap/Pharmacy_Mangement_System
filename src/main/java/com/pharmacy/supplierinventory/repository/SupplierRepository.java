// SupplierRepository.java
package com.pharmacy.supplierinventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pharmacy.supplierinventory.entity.Supplier;

public interface SupplierRepository extends MongoRepository<Supplier, String> {
}
