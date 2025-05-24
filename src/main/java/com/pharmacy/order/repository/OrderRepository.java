//repository.interface
package com.pharmacy.order.repository;

import com.pharmacy.order.entity.Order;
import com.pharmacy.order.entity.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByDoctorId(String orderId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
