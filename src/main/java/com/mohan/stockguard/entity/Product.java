package com.mohan.stockguard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Product with stock tracking.
 * The @Version field enables optimistic locking: JPA auto-checks this
 * column on every UPDATE and throws OptimisticLockException if another
 * transaction modified the row in between (classic lost-update prevention).
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;

    // Used by the OPTIMISTIC locking strategy (Week 2, Day 8-10)
    @Version
    private Long version;
}
