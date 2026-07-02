package com.mohan.stockguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohan.stockguard.dto.PlaceOrderRequest;
import com.mohan.stockguard.entity.Order;
import com.mohan.stockguard.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void placeOrderUsesOptimisticStrategyByDefault() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        Order expectedOrder = Order.builder()
            .id(1L)
            .productId(1L)
            .userId(1L)
            .quantity(1)
            .status(Order.OrderStatus.CONFIRMED)
            .createdAt(Instant.now())
            .build();

        Mockito.when(orderService.placeOrderOptimistic(eq(1L), eq(1L), eq(1))).thenReturn(expectedOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    void placeOrderUsesPessimisticStrategyWhenRequested() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        Order expectedOrder = Order.builder()
            .id(2L)
            .productId(1L)
            .userId(1L)
            .quantity(1)
            .status(Order.OrderStatus.CONFIRMED)
            .createdAt(Instant.now())
            .build();

        Mockito.when(orderService.placeOrderPessimistic(eq(1L), eq(1L), eq(1))).thenReturn(expectedOrder);

        mockMvc.perform(post("/api/orders?strategy=pessimistic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.productId").value(1));
    }
}
