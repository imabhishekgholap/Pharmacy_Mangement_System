// DrugClient.java
package com.pharmacy.supplierinventory.feign;

import com.pharmacy.supplierinventory.model.DrugDTO;
import com.pharmacy.supplierinventory.model.RestockDrugDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "drug-service", url = "http://localhost:8081/api/drugs")
public interface DrugClient {
    @GetMapping("/batch/{batchId}")
    DrugDTO getDrugByBatchId(@PathVariable("batchId") String batchId);
    
//    @PostMapping("/restock/{batchId}/{quantityToAdd}")
//    void restock(@PathVariable String batchId,@PathVariable int quantityToAdd);    
    
    
  @PostMapping("/restock")
  void restock(@RequestBody RestockDrugDTO drugDTO); 
  
}

