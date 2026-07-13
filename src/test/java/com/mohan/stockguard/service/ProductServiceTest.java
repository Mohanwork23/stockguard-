package com.mohan.stockguard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCacheService productCacheService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductService productService;

    ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProductEvictsCache() {
        Product product = Product.builder()
            .name("Test")
            .price(new BigDecimal("10.00"))
            .availableStock(10)
            .build();

        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.createProduct(product);

        assertThat(saved).isEqualTo(product);
        verify(productCacheService).evictAll();
    }

    @Test
    void updateProductEvictsCache() {
        Product existing = Product.builder()
            .id(1L)
            .name("Existing")
            .price(new BigDecimal("20.00"))
            .availableStock(5)
            .build();

        Product update = Product.builder()
            .name("Updated")
            .price(new BigDecimal("25.00"))
            .availableStock(7)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product saved = productService.updateProduct(1L, update);

        assertThat(saved.getName()).isEqualTo("Updated");
        assertThat(saved.getPrice()).isEqualTo(new BigDecimal("25.00"));
        verify(productCacheService).evictAll();
        verify(productCacheService).evictProduct(1L);
    }

    @Test
    void getLowStockProductsReturnsLowStockItems() {
        Product product = Product.builder()
            .id(2L)
            .name("Low Stock Item")
            .price(new BigDecimal("15.00"))
            .availableStock(4)
            .build();

        when(productRepository.findTop10ByAvailableStockLessThanOrderByAvailableStockAsc(10))
            .thenReturn(List.of(product));

        List<Product> result = productService.getLowStockProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Low Stock Item");
    }
}
