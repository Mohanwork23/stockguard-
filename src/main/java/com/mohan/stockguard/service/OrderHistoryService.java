package com.mohan.stockguard.service;

import com.mohan.stockguard.dto.OrderHistoryResponse;
import com.mohan.stockguard.dto.PaginatedResponse;
import com.mohan.stockguard.entity.Order;
import com.mohan.stockguard.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderRepository orderRepository;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Transactional(readOnly = true)
    public PaginatedResponse<OrderHistoryResponse> getUserOrderHistory(Long userId, Integer pageNumber, Integer pageSize) {
        int page = pageNumber != null ? pageNumber : 0;
        int size = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(order -> new Object[]{order});
        
        // Simplified mapping - just use the repository results directly
        var content = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .stream()
            .map(OrderHistoryResponse::fromOrder)
            .toList();

        Page<Object> pageResult = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(o -> (Object) o);

        return PaginatedResponse.<OrderHistoryResponse>builder()
            .content(content)
            .pageNumber(page)
            .pageSize(size)
            .totalElements(pageResult.getTotalElements())
            .totalPages(pageResult.getTotalPages())
            .hasMore(pageResult.hasNext())
            .build();
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<OrderHistoryResponse> getUserOrderHistoryByStatus(Long userId, String status, Integer pageNumber, Integer pageSize) {
        int page = pageNumber != null ? pageNumber : 0;
        int size = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Object> pageResult = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable)
            .map(o -> (Object) o);

        var content = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable)
            .stream()
            .map(OrderHistoryResponse::fromOrder)
            .toList();

        return PaginatedResponse.<OrderHistoryResponse>builder()
            .content(content)
            .pageNumber(page)
            .pageSize(size)
            .totalElements(pageResult.getTotalElements())
            .totalPages(pageResult.getTotalPages())
            .hasMore(pageResult.hasNext())
            .build();
    }

    @Transactional(readOnly = true)
    public List<Order> getRecentOrdersForUser(Long userId, int limit) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit)).getContent();
    }
}
