package com.pharmacy.order.feign;

import com.pharmacy.order.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sales-service", url = "http://localhost:8084")
public interface SalesClient {

    @PostMapping("/sales/record")
    void recordSale(@RequestBody SalesDTO salesDTO);
}