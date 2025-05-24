package com.pharmacy.supplierinventory.entity;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "inventories")
public class Inventory {
    @Id
    private String inventoryId;

    @NotBlank(message = "Drug batch ID is required")
    private String drugBatchId;

    @NotBlank(message = "Supplier ID is required")
    private String supplierId;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;
}
