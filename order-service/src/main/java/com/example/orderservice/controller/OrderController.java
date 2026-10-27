package com.example.orderservice.controller;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderController(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "order-service");
        return ResponseEntity.ok(status);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order orderRequest) {
        // Inter-service call: check product and price from Product-Service
        Map<String, Object> product = productClient.getProduct(orderRequest.getProductId());
        if (product == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Product not found or Product-Service unavailable.");
            return ResponseEntity.badRequest().body(error);
        }

        Number priceNum = (Number) product.get("price");
        double unitPrice = priceNum != null ? priceNum.doubleValue() : 0.0;
        int stockQuantity = ((Number) product.get("stockQuantity")).intValue();

        if (stockQuantity < orderRequest.getQuantity()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Requested quantity (" + orderRequest.getQuantity() + ") exceeds available stock (" + stockQuantity + ").");
            return ResponseEntity.badRequest().body(error);
        }

        // Inter-service call: reduce stock in Product-Service
        boolean stockReduced = productClient.reduceStock(orderRequest.getProductId(), orderRequest.getQuantity());
        if (!stockReduced) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to reserve inventory with Product-Service.");
            return ResponseEntity.badRequest().body(error);
        }

        orderRequest.setTotalPrice(unitPrice * orderRequest.getQuantity());
        orderRequest.setStatus("COMPLETED");
        Order savedOrder = orderRepository.save(orderRequest);

        return ResponseEntity.ok(savedOrder);
    }
}
