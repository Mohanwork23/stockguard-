package com.mohan.stockguard.service;

import com.mohan.stockguard.dto.InventoryAnalyticsResponse;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.OrderRepository;
import com.mohan.stockguard.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class InventoryAnalyticsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private InventoryAnalyticsService inventoryAnalyticsService;

    InventoryAnalyticsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAnalyticsReturnsLowStockAlerts() {
        Product product = Product.builder()
            .id(1L)
            .name("Keyboard")
            .price(new BigDecimal("49.99"))
            .availableStock(5)
            .build();

        when(productRepository.count()).thenReturn(1L);
        when(productRepository.countOutOfStock()).thenReturn(0L);
        when(productRepository.countLowStock()).thenReturn(1L);
        when(productRepository.findTop10ByAvailableStockLessThanOrderByAvailableStockAsc(10)).thenReturn(List.of(product));
        when(orderRepository.countOrdersByProductId(1L)).thenReturn(3);

        InventoryAnalyticsResponse response = inventoryAnalyticsService.getAnalytics();

        assertThat(response.getLowStockCount()).isEqualTo(1L);
        assertThat(response.getLowStockAlerts()).hasSize(1);
        assertThat(response.getLowStockAlerts().get(0).getProductName()).isEqualTo("Keyboard");
    }
}
