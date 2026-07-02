package com.mohan.stockguard.repository;

import com.mohan.stockguard.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
