package com.pharmacy.supplierinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.pharmacy.supplierinventory.feign")
public class SupplierInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplierInventoryServiceApplication.class, args);
	}

}
