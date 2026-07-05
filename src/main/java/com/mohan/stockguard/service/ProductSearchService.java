package com.mohan.stockguard.service;

import com.mohan.stockguard.dto.PaginatedResponse;
import com.mohan.stockguard.dto.ProductSearchRequest;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Transactional(readOnly = true)
    public PaginatedResponse<Product> searchProducts(ProductSearchRequest request) {
        int pageNumber = request.getPageNumber() != null ? request.getPageNumber() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : DEFAULT_PAGE_SIZE;
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        
        Boolean lowStockOnly = request.getLowStockOnly() != null ? request.getLowStockOnly() : false;
        
        Page<Product> page = productRepository.searchProducts(
            request.getName(),
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getMinStock(),
            lowStockOnly,
            pageable
        );

        return PaginatedResponse.<Product>builder()
            .content(page.getContent())
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .hasMore(page.hasNext())
            .build();
    }
}
