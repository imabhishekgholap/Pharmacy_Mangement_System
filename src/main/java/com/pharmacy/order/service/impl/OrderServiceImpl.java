package com.pharmacy.order.service.impl;

import com.pharmacy.order.entity.Order;
import com.pharmacy.order.entity.OrderStatus;
import com.pharmacy.order.exception.*;
import com.pharmacy.order.feign.DrugServiceClient;
import com.pharmacy.order.feign.SalesClient;
import com.pharmacy.order.model.DrugDTO;
import com.pharmacy.order.model.SalesDTO;
import com.pharmacy.order.repository.OrderRepository;
import com.pharmacy.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final DrugServiceClient drugServiceClient;
    private final SalesClient salesClient;

    public OrderServiceImpl(OrderRepository orderRepository, DrugServiceClient drugServiceClient, SalesClient salesClient) {
        this.orderRepository = orderRepository;
        this.drugServiceClient = drugServiceClient;
        this.salesClient = salesClient;
    }

    @Override
    public Order createOrder(Order order) {
        logger.info("Creating order for batchId: {}", order.getBatchId());

        // Validate order input
        if (order.getBatchId() == null || order.getBatchId().isEmpty()) {
            throw new InvalidOrderException("Batch ID is required");
        }
        if (order.getQuantity() <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero");
        }

        // Fetch drug details from drug service
        DrugDTO drug = drugServiceClient.getDrugByBatchId(order.getBatchId());
        if (drug == null) {
            throw new DrugNotFoundException("Drug not found with batch ID: " + order.getBatchId());
        }

        // Populate order fields based on drug info
        order.setDrugName(drug.getName());
        order.setPrice(drug.getPrice());
        order.setTotalAmount(drug.getPrice() * order.getQuantity());
        order.setId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        
        // Deduct drug
        drugServiceClient.reduceDrugQuantity(order.getBatchId(), order.getQuantity());
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(String id) {
        logger.info("Fetching order with ID: {}", id);
        // Throws exception if not found
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    @Override
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) 
        	throw new OrderNotFoundException("No orders found");
        return orders;
    }

    @Override
    public Order updateOrder(String id, Order order) {
        logger.info("Updating order with ID: {}", id);

        // Ensure the order exists
        Order existing = getOrderById(id);

        if (order.getQuantity() <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero");
        }

        // Update doctor and quantity info
        existing.setDoctorId(order.getDoctorId());
        existing.setDoctorName(order.getDoctorName());
        existing.setDoctorContact(order.getDoctorContact());
        existing.setDoctorEmail(order.getDoctorEmail());
        existing.setQuantity(order.getQuantity());

        // Re-fetch drug details in case price changed
        DrugDTO drug = drugServiceClient.getDrugByBatchId(order.getBatchId());
        existing.setDrugName(drug.getName());
        existing.setPrice(drug.getPrice());
        existing.setTotalAmount(drug.getPrice() * order.getQuantity());

        return orderRepository.save(existing);
    }

    @Override
    public void deleteOrder(String id) {
        logger.info("Deleting order with ID: {}", id);
        Order order = getOrderById(id);

        // Do not allow deletion of completed orders
        if (order.getStatus() == OrderStatus.PICKED_UP) {
            throw new InvalidOrderException("Cannot delete picked-up order");
        }

        orderRepository.delete(order);
    }

    @Override
    public List<Order> getOrdersByDoctor(String doctorId) {
        logger.info("Fetching orders for doctorId: {}", doctorId);
        List<Order> orders = orderRepository.findByDoctorId(doctorId);
        if (orders.isEmpty()) throw new OrderNotFoundException("No orders found for doctor ID: " + doctorId);
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        logger.info("Fetching orders with status: {}", status);

        try {
            // Convert string to enum
            OrderStatus s = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findByStatus(s);
            if (orders.isEmpty()) throw new OrderNotFoundException("No orders found with status: " + status);
            return orders;
        } catch (IllegalArgumentException e) {
            // Catch invalid enum values
            logger.error("Invalid status: {}", status);
            throw new InvalidOrderException("Invalid status: " + status);
        }
    }

    @Override
    public Order verifyOrder(String id) {
        logger.info("Verifying order with ID: {}", id);
        Order order = getOrderById(id);

        // Only allow verification of pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderException("Only PENDING orders can be verified");
        }

        // Ensure drug stock is available
        try {
            drugServiceClient.checkDrugAvailability(order.getBatchId(), order.getQuantity());
        } catch (Exception e) {
            logger.error("Drug service unavailable during availability check: {}", e.getMessage(), e);
            throw new RuntimeException("Drug service unavailable: " + e.getMessage(), e);
        }

        // Update order status to verified
        order.setStatus(OrderStatus.VERIFIED);
        Order savedOrder = orderRepository.save(order);

        // Record sale in sales service
        try {
            SalesDTO salesDTO = new SalesDTO();
            salesDTO.setOrderId(savedOrder.getId());
            salesDTO.setBatchId(savedOrder.getBatchId());
            salesDTO.setQuantity(savedOrder.getQuantity());
            salesDTO.setTotalPrice(savedOrder.getTotalAmount());
            salesDTO.setPaidAmount(savedOrder.getPaidAmount());
            salesDTO.setDoctorName(savedOrder.getDoctorName());
            salesDTO.setDrugName(savedOrder.getDrugName());

            salesClient.recordSale(salesDTO);
            logger.info("Sale recorded for orderId: {}", savedOrder.getId());
        } catch (Exception e) {
            logger.error("Failed to record sale: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to record sale: Sales service unavailable", e);
        }
        return savedOrder;
    }

    @Override
    public Order markAsPickedUp(String id, LocalDateTime pickupDate) {
        logger.info("Marking order as picked up with ID: {}", id);
        Order order = getOrderById(id);

        // Only verified orders can be picked up
        if (order.getStatus() != OrderStatus.VERIFIED) {
            throw new InvalidOrderException("Order must be verified before pickup");
        }

        order.setStatus(OrderStatus.PICKED_UP);
        order.setPickupDate(pickupDate);

        return orderRepository.save(order);
    }
    
    public Map<String, Object> calculateTotalPrice(String batchId, int quantity) throws InvalidOrderException {
	    DrugDTO drug = drugServiceClient.getDrugByBatchId(batchId);
	    if (drug.getQuantity() < quantity) {
	        throw new InvalidOrderException("Not enough stock for batch " + batchId);
	    }

	    double totalPrice = drug.getPrice() * quantity;

	    Map<String, Object> result = new HashMap<>();
	    result.put("unitPrice", drug.getPrice());
	    result.put("totalPrice", totalPrice);
	    result.put("availableStock", drug.getQuantity());
	    return result;
	}
}
