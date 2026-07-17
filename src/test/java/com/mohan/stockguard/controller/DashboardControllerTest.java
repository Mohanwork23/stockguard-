package com.mohan.stockguard.controller;

import com.mohan.stockguard.repository.OrderRepository;
import com.mohan.stockguard.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void dashboardEndpointReturnsSummaryCounts() throws Exception {
        when(productRepository.count()).thenReturn(5L);
        when(orderRepository.count()).thenReturn(12L);
        when(productRepository.countLowStock()).thenReturn(2L);

        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").value(5))
            .andExpect(jsonPath("$.orders").value(12))
            .andExpect(jsonPath("$.lowStockProducts").value(2));
    }
}
