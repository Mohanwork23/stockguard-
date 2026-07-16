package com.mohan.stockguard.controller;

import com.mohan.stockguard.repository.OrderRepository;
import com.mohan.stockguard.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void metricsEndpointReturnsKeyCounts() throws Exception {
        when(productRepository.count()).thenReturn(3L);
        when(orderRepository.count()).thenReturn(7L);

        mockMvc.perform(get("/api/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").value(3))
            .andExpect(jsonPath("$.orders").value(7));
    }
}
