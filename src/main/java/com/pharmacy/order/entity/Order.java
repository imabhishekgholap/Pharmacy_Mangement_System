package com.pharmacy.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    private String id;

//    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Doctor contact is required")
    private String doctorContact;

    @NotBlank(message = "Doctor email is required")
    @Email(message = "Invalid email format")
    private String doctorEmail;

    @NotBlank(message = "Batch ID is required")
    private String batchId;

    private String drugName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private double price;

    private LocalDateTime orderDate;
    
    private LocalDateTime pickupDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @DecimalMin(value = "0.0", message = "Paid amount cannot be negative")
    private double paidAmount;

    private double totalAmount;
}
