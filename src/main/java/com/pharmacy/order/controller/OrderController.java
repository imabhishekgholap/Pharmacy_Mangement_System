package com.pharmacy.order.controller;

import com.pharmacy.order.entity.Order;
import com.pharmacy.order.exception.InvalidOrderException;
import com.pharmacy.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return new ResponseEntity<>(orderService.createOrder(order), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Order>> getOrdersByDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(orderService.getOrdersByDoctor(doctorId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Order> verifyOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.verifyOrder(id));
    }

//    @PostMapping("/{id}/pickup")
//    public ResponseEntity<Order> markAsPickedUp(@PathVariable String id, @RequestParam String pickupDate) {
//        return ResponseEntity.ok(orderService.markAsPickedUp(id, LocalDateTime.parse(pickupDate)));
//    } 
    @PostMapping("/{id}/pickup")
    public ResponseEntity<Order> markAsPickedUp(@PathVariable String id, @RequestParam String pickupDate) {
        try {
            System.out.println("Received pickupDate: " + pickupDate);
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(pickupDate); // handles 'Z'
            LocalDateTime localDateTime = offsetDateTime.toLocalDateTime(); // convert to LocalDateTime if needed
            return ResponseEntity.ok(orderService.markAsPickedUp(id, localDateTime));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/price-stock")
    public ResponseEntity<Map<String, Object>> getPriceAndStockInfo(
            @RequestParam("batchId") String batchId,
            @RequestParam("quantity") int quantity) {
        try {
            Map<String, Object> response = orderService.calculateTotalPrice(batchId, quantity);
            return ResponseEntity.ok(response);
        } catch (InvalidOrderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Something went wrong"));
        }
    }
//    @PostMapping("/{id}/verify-for-sale")
//    public ResponseEntity<String> verifyAndRecordSale(@PathVariable String id) {
//        String message = orderService.verifyAndRecordSale(id);
//        return ResponseEntity.ok(message);
//    }

}
