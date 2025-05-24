package com.pharmacy.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.order.model.DrugDTO;

@FeignClient(name = "drug-service", url = "http://localhost:8081")
public interface DrugServiceClient {
    @GetMapping("/api/drugs/batch/{batchId}")
    DrugDTO getDrugByBatchId(@PathVariable String batchId);

    @PostMapping("/api/drugs/batch/{batchId}/check-availability")
    void checkDrugAvailability(@PathVariable String batchId, @RequestParam int quantity);

    @PostMapping("/api/drugs/batch/{batchId}/reduce-quantity")
    void reduceDrugQuantity(@PathVariable String batchId, @RequestParam int quantity);
}
