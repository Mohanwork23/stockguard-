package com.mohan.stockguard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReadinessController {

    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> ready() {
        return ResponseEntity.ok(Map.of("status", "READY"));
    }
}
