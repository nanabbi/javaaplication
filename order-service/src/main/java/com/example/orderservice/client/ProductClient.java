package com.example.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String productBaseUrl;

    public ProductClient(RestTemplate restTemplate, @Value("${services.product.url}") String productBaseUrl) {
        this.restTemplate = restTemplate;
        this.productBaseUrl = productBaseUrl;
    }

    public Map<String, Object> getProduct(Long productId) {
        try {
            return restTemplate.getForObject(productBaseUrl + "/" + productId, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean reduceStock(Long productId, Integer quantity) {
        try {
            String url = productBaseUrl + "/" + productId + "/reduce-stock?quantity=" + quantity;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
