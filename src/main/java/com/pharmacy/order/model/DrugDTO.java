package com.pharmacy.order.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DrugDTO {
    private String name;
    private String batchId;
    private int quantity;
    private LocalDate expiryDate;
    private double price;
    private String supplierEmail;
}
