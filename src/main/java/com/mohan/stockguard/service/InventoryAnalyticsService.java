package com.mohan.stockguard.service;

import com.mohan.stockguard.dto.InventoryAnalyticsResponse;
import com.mohan.stockguard.dto.InventoryAnalyticsResponse.LowStockAlertDto;
import com.mohan.stockguard.dto.InventoryAnalyticsResponse.TopProductDto;
import com.mohan.stockguard.repository.OrderRepository;
import com.mohan.stockguard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryAnalyticsService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Transactional(readOnly = true)
    public InventoryAnalyticsResponse getAnalytics() {
        long totalProducts = productRepository.count();
        long outOfStockCount = productRepository.countOutOfStock();
        long lowStockCount = productRepository.countLowStock();

        var topProducts = productRepository.findAll().stream()
            .limit(5)
            .map(p -> TopProductDto.builder()
                .productId(p.getId())
                .productName(p.getName())
                .totalOrdered(orderRepository.countOrdersByProductId(p.getId()))
                .build())
            .collect(Collectors.toList());

        var lowStockAlerts = productRepository.findTop10ByAvailableStockLessThanOrderByAvailableStockAsc(LOW_STOCK_THRESHOLD).stream()
            .map(p -> LowStockAlertDto.builder()
                .productId(p.getId())
                .productName(p.getName())
                .currentStock(p.getAvailableStock())
                .threshold(LOW_STOCK_THRESHOLD)
                .build())
            .collect(Collectors.toList());

        return InventoryAnalyticsResponse.builder()
            .totalProducts(totalProducts)
            .outOfStockCount(outOfStockCount)
            .lowStockCount(lowStockCount)
            .topProducts(topProducts)
            .lowStockAlerts(lowStockAlerts)
            .build();
    }
}
