package com.pharmacy.order.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesDTO{
    private String orderId;
    private String batchId;
    private int quantity;
    private double totalPrice;
    private double paidAmount;
    private String doctorName;
    private String drugName;
}