package com.pharmacy.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.pharmacy.order.entity.Order;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(String id);
    List<Order> getAllOrders();
    Order updateOrder(String id, Order order);
    void deleteOrder(String id);
    List<Order> getOrdersByDoctor(String doctorId);
    List<Order> getOrdersByStatus(String status);
    Order verifyOrder(String id);
    Order markAsPickedUp(String id, LocalDateTime pickupDate);
    Map<String, Object> calculateTotalPrice(String batchId, int quantity);
}
