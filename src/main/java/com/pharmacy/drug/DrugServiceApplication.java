package com.pharmacy.drug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DrugServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(DrugServiceApplication.class, args);
	}

}
