package com.mohan.stockguard.service;

import com.mohan.stockguard.dto.PaginatedResponse;
import com.mohan.stockguard.dto.ProductSearchRequest;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    ProductSearchServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchProductsReturnsPaginatedResults() {
        Product product = Product.builder()
            .id(1L)
            .name("Laptop")
            .price(new BigDecimal("999.99"))
            .availableStock(12)
            .build();

        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        when(productRepository.searchProducts(any(), any(), any(), any(), any(), any()))
            .thenReturn(page);

        ProductSearchRequest request = ProductSearchRequest.builder()
            .name("lap")
            .pageNumber(0)
            .pageSize(20)
            .build();

        PaginatedResponse<Product> result = productSearchService.searchProducts(request);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
    }
}
