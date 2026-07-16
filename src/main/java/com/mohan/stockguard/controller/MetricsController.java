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
public class MetricsController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> metrics() {
        return ResponseEntity.ok(Map.of(
            "products", productRepository.count(),
            "orders", orderRepository.count()
        ));
    }
}
