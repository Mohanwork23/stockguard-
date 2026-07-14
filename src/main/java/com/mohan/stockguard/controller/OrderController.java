package com.mohan.stockguard.controller;

import com.mohan.stockguard.dto.OrderHistoryResponse;
import com.mohan.stockguard.dto.PaginatedResponse;
import com.mohan.stockguard.dto.PlaceOrderRequest;
import com.mohan.stockguard.entity.Order;
import com.mohan.stockguard.security.UserDetailsImpl;
import com.mohan.stockguard.service.OrderHistoryService;
import com.mohan.stockguard.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(
        @Valid @RequestBody PlaceOrderRequest request,
        @RequestParam(name = "strategy", defaultValue = "optimistic") String strategy,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        Order order;
        if ("pessimistic".equalsIgnoreCase(strategy)) {
            order = orderService.placeOrderPessimistic(
                request.getProductId(), userId, request.getQuantity());
        } else {
            order = orderService.placeOrderOptimistic(
                request.getProductId(), userId, request.getQuantity());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/history")
    public ResponseEntity<PaginatedResponse<OrderHistoryResponse>> getOrderHistory(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "20") Integer pageSize,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        PaginatedResponse<OrderHistoryResponse> history = orderHistoryService.getUserOrderHistory(userId, pageNumber, pageSize);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/status/{status}")
    public ResponseEntity<PaginatedResponse<OrderHistoryResponse>> getOrderHistoryByStatus(
        @PathVariable String status,
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "20") Integer pageSize,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        PaginatedResponse<OrderHistoryResponse> history = orderHistoryService.getUserOrderHistoryByStatus(userId, status, pageNumber, pageSize);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders(
        @RequestParam(defaultValue = "5") int limit,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        return ResponseEntity.ok(orderHistoryService.getRecentOrdersForUser(userId, limit));
    }
}

