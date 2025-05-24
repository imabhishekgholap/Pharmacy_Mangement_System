package com.pharmacy.order;

import com.pharmacy.order.entity.Order;
import com.pharmacy.order.entity.OrderStatus;
import com.pharmacy.order.exception.*;
import com.pharmacy.order.feign.DrugServiceClient;
import com.pharmacy.order.feign.SalesClient;
import com.pharmacy.order.model.DrugDTO;
import com.pharmacy.order.repository.OrderRepository;
import com.pharmacy.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceApplicationTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DrugServiceClient drugServiceClient;

    @Mock
    private SalesClient salesClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setBatchId("B001");
        order.setQuantity(10);
        order.setDoctorId("D001");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
    }

    // 1. Test for creating an order
    @Test
    void testCreateOrder() {
        DrugDTO drug = new DrugDTO();
        drug.setName("Paracetamol");
        drug.setPrice(20.0);

        when(drugServiceClient.getDrugByBatchId(order.getBatchId())).thenReturn(drug);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order saved = orderService.createOrder(order);
        assertEquals(order.getBatchId(), saved.getBatchId());
    }

    // 2. Test for creating an order with invalid batch ID
    @Test
    void testCreateOrderWithInvalidBatchId() {
        order.setBatchId("");
        assertThrows(InvalidOrderException.class, () -> orderService.createOrder(order));
    }

    // 3. Test for fetching an order by ID
    @Test
    void testGetOrderById() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        Order found = orderService.getOrderById(order.getId());
        assertEquals(order.getBatchId(), found.getBatchId());
    }

    // 4. Test for fetching an order by ID when not found
    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(order.getId()));
    }

    // 5. Test for fetching all orders
    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        List<Order> orders = orderService.getAllOrders();
        assertFalse(orders.isEmpty());
    }

    // 6. Test for deleting an order
    @Test
    void testDeleteOrder() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        assertDoesNotThrow(() -> orderService.deleteOrder(order.getId()));
    }

    // 7. Test for deleting a picked-up order
    @Test
    void testDeletePickedUpOrder() {
        order.setStatus(OrderStatus.PICKED_UP);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(InvalidOrderException.class, () -> orderService.deleteOrder(order.getId()));
    }

    // 8. Test for fetching orders by doctor ID
    @Test
    void testGetOrdersByDoctor() {
        when(orderRepository.findByDoctorId(order.getDoctorId())).thenReturn(List.of(order));
        List<Order> result = orderService.getOrdersByDoctor(order.getDoctorId());
        assertEquals(1, result.size());
    }

    // 9. Test for fetching orders by status
    @Test
    void testGetOrdersByStatus() {
        when(orderRepository.findByStatus(order.getStatus())).thenReturn(List.of(order));
        List<Order> result = orderService.getOrdersByStatus(order.getStatus().name());
        assertEquals(1, result.size());
    }

    // 10. Test for invalid status when fetching orders by status
    @Test
    void testGetOrdersByInvalidStatus() {
        assertThrows(InvalidOrderException.class, () -> orderService.getOrdersByStatus("INVALID_STATUS"));
    }

    // 11. Test for verifying an order
    @Test
    void testVerifyOrder() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(drugServiceClient).checkDrugAvailability(order.getBatchId(), order.getQuantity());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order verifiedOrder = orderService.verifyOrder(order.getId());
        assertEquals(OrderStatus.VERIFIED, verifiedOrder.getStatus());
    }

    // 12. Test for verifying a non-pending order
    @Test
    void testVerifyNonPendingOrder() {
        order.setStatus(OrderStatus.VERIFIED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(InvalidOrderException.class, () -> orderService.verifyOrder(order.getId()));
    }

    // 13. Test for marking an order as picked up when not verified
    @Test
    void testMarkAsPickedUpWithoutVerification() {
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(InvalidOrderException.class, () -> orderService.markAsPickedUp(order.getId(), LocalDateTime.now()));
    }
    
}
