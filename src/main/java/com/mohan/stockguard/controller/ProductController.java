package com.mohan.stockguard.controller;

import com.mohan.stockguard.dto.ProductRequest;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.service.ProductService;
import com.mohan.stockguard.service.RateLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RateLimitService rateLimitService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        if (!rateLimitService.tryConsume(getRateLimitKey(clientId))) {
            return ResponseEntity.status(429).build();
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(
        @PathVariable("id") Long id,
        @RequestHeader(value = "X-Client-Id", required = false) String clientId
    ) {
        if (!rateLimitService.tryConsume(getRateLimitKey(clientId))) {
            return ResponseEntity.status(429).build();
        }
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product saved = productService.createProduct(request.toProduct());
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
        @PathVariable("id") Long id,
        @Valid @RequestBody ProductRequest request
    ) {
        Product updated = productService.updateProduct(id, request.toProduct());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private String getRateLimitKey(String clientId) {
        return clientId != null ? clientId : "anonymous";
    }
}
