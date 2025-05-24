package com.pharmacy.sales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Order ID is required")
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @NotBlank(message = "Batch ID is required")
    private String batchId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @PositiveOrZero(message = "Total price must be zero or positive")
    private double totalPrice;

    @PositiveOrZero(message = "Paid amount must be zero or positive")
    private double paidAmount;

    @PositiveOrZero(message = "Balance must be zero or positive")
    private double balance;

    @NotBlank(message = "Drug name is required")
    private String drugName;

    @PastOrPresent(message = "Sale date cannot be in the future")
    @Column(name = "sale_date")
    private LocalDate saleDate;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;
}
