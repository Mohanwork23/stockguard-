package com.mohan.stockguard.repository;

import com.mohan.stockguard.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    // Search and filter queries
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minStock IS NULL OR p.availableStock >= :minStock) AND " +
           "(:lowStockOnly = FALSE OR p.availableStock < 10)")
    Page<Product> searchProducts(
        @Param("name") String name,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minStock") Integer minStock,
        @Param("lowStockOnly") Boolean lowStockOnly,
        Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Product p WHERE p.availableStock = 0")
    Long countOutOfStock();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.availableStock > 0 AND p.availableStock < 10")
    Long countLowStock();
}
