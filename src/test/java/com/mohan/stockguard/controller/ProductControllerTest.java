package com.mohan.stockguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.service.ProductService;
import com.mohan.stockguard.service.RateLimitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private RateLimitService rateLimitService;

    @Test
    void getAllProductsReturnsProductsWhenNotRateLimited() throws Exception {
        Product product = Product.builder()
            .id(1L)
            .name("Widget")
            .price(new BigDecimal("19.99"))
            .availableStock(50)
            .build();

        when(rateLimitService.tryConsume(anyString())).thenReturn(true);
        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products")
                .header("X-Client-Id", "client-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Widget"));
    }

    @Test
    void getAllProductsReturns429WhenRateLimited() throws Exception {
        when(rateLimitService.tryConsume(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/products")
                .header("X-Client-Id", "client-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }
}
