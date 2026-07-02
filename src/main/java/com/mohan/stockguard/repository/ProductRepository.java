package com.mohan.stockguard.repository;

import com.mohan.stockguard.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Plain read -> used with @Version for the OPTIMISTIC locking flow.
    Optional<Product> findById(Long id);

    /**
     * PESSIMISTIC_WRITE issues SELECT ... FOR UPDATE, locking the row
     * until the transaction commits/rolls back. Used in Week 2, Day 11-12
     * to compare against the optimistic approach under load.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
