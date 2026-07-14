package com.mohan.stockguard.service;

import com.mohan.stockguard.entity.Order;
import com.mohan.stockguard.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class OrderHistoryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderHistoryService orderHistoryService;

    OrderHistoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRecentOrdersForUserReturnsRecentOrders() {
        Order order = Order.builder().id(1L).status("COMPLETED").build();
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 3)))
            .thenReturn(new PageImpl<>(List.of(order)));

        List<Order> result = orderHistoryService.getRecentOrdersForUser(1L, 3);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }
}
