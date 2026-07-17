package com.mohan.stockguard.controller;

import com.mohan.stockguard.repository.OrderRepository;
import com.mohan.stockguard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> dashboard() {
        return ResponseEntity.ok(Map.of(
            "products", productRepository.count(),
            "orders", orderRepository.count(),
            "lowStockProducts", productRepository.countLowStock()
        ));
    }
}
