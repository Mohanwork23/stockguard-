package com.mohan.stockguard.controller;

import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExportController {

    private final ProductRepository productRepository;

    @GetMapping("/export/products")
    public ResponseEntity<List<Product>> exportProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }
}
