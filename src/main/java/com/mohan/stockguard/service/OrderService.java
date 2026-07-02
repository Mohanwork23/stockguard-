package com.mohan.stockguard.service;

import com.mohan.stockguard.dto.OrderEvent;
import com.mohan.stockguard.entity.Order;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.exception.InsufficientStockException;
import com.mohan.stockguard.repository.OrderRepository;
import com.mohan.stockguard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * TODO (Week 2 plan):
 *   1. placeOrderOptimistic() - implemented below. Relies on Product.version
 *      (@Version). Under concurrent access, the loser transaction throws
 *      OptimisticLockingFailureException and Spring Retry retries it a few
 *      times with backoff.
 *   2. placeOrderPessimistic() - add a second method using
 *      productRepository.findByIdForUpdate() (SELECT ... FOR UPDATE) and
 *      compare throughput/latency against the optimistic version under the
 *      k6 load test in /loadtest.
 *   3. Write the comparison writeup in README under "Concurrency Findings"
 *      once both are load-tested - this becomes your resume metric.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductCacheService productCacheService;
    private final OrderEventPublisher orderEventPublisher;

    @Retryable(
        retryFor = OptimisticLockingFailureException.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public Order placeOrderOptimistic(Long productId, Long userId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getAvailableStock() < quantity) {
            throw new InsufficientStockException(
                "Requested " + quantity + " but only " + product.getAvailableStock() + " available");
        }

        product.setAvailableStock(product.getAvailableStock() - quantity);
        productRepository.save(product); // version check happens here on flush
        productCacheService.evictProduct(productId);

        Order order = Order.builder()
            .productId(productId)
            .userId(userId)
            .quantity(quantity)
            .status(Order.OrderStatus.CONFIRMED)
            .createdAt(Instant.now())
            .build();

        Order savedOrder = orderRepository.save(order);
        publishOrderEvent(savedOrder);
        return savedOrder;
    }

    // Week 2, Day 11-12: implement placeOrderPessimistic() here using
    // productRepository.findByIdForUpdate(productId) instead, wrapped in
    // @Transactional, with NO @Retryable needed since the DB row lock
    // serializes access instead of relying on optimistic retry.

    @Transactional
    public Order placeOrderPessimistic(Long productId, Long userId, int quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getAvailableStock() < quantity) {
            throw new InsufficientStockException(
                "Requested " + quantity + " but only " + product.getAvailableStock() + " available");
        }

        product.setAvailableStock(product.getAvailableStock() - quantity);
        productRepository.save(product);
        productCacheService.evictProduct(productId);

        Order order = Order.builder()
            .productId(productId)
            .userId(userId)
            .quantity(quantity)
            .status(Order.OrderStatus.CONFIRMED)
            .createdAt(Instant.now())
            .build();

        Order savedOrder = orderRepository.save(order);
        publishOrderEvent(savedOrder);
        return savedOrder;
    }

    private void publishOrderEvent(Order savedOrder) {
        OrderEvent event = new OrderEvent(
            savedOrder.getId(),
            savedOrder.getUserId(),
            savedOrder.getProductId(),
            savedOrder.getQuantity(),
            savedOrder.getStatus().name(),
            savedOrder.getCreatedAt()
        );
        orderEventPublisher.publish(event);
    }
}
