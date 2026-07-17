package com.mohan.stockguard.controller;

import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExportController.class)
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Test
    void exportProductsEndpointReturnsProducts() throws Exception {
        Product product = Product.builder()
            .id(1L)
            .name("Notebook")
            .price(new BigDecimal("29.99"))
            .availableStock(10)
            .build();

        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/export/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Notebook"));
    }
}
