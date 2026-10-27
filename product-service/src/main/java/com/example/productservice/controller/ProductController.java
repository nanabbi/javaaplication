package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "product-service");
        return ResponseEntity.ok(status);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Inter-service inventory deduction endpoint
    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<Map<String, Object>> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        return productRepository.findById(id).map(product -> {
            if (product.getStockQuantity() < quantity) {
                response.put("success", false);
                response.put("message", "Insufficient stock available");
                return ResponseEntity.badRequest().body(response);
            }
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
            response.put("success", true);
            response.put("remainingStock", product.getStockQuantity());
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("success", false);
            response.put("message", "Product not found");
            return ResponseEntity.status(404).body(response);
        });
    }
}
