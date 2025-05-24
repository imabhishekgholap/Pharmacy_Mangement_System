package com.pharmacy.sales.model;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesDTO {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Batch ID is required")
    private String batchId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @PositiveOrZero(message = "Total price must be zero or positive")
    private double totalPrice;

    @PositiveOrZero(message = "Paid amount must be zero or positive")
    private double paidAmount;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Drug name is required")
    private String drugName;
}
