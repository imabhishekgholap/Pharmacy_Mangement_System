package com.pharmacy.supplierinventory.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockDrugDTO {
    private String batchId;
    private int quantityToAdd;
    private LocalDate expiryDate;
    private String email;
}
