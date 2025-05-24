package com.pharmacy.drug.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Drug {

    @Id
    @NotBlank(message = "Batch ID cannot be blank")
    private String batchId;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private int quantity;

    private LocalDate expiryDate;

    @Min(value = 1, message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Supplier email cannot be blank")
    @Email(message = "Invalid email format")
    private String supplierEmail;
}
