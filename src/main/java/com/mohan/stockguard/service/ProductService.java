package com.mohan.stockguard.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCacheService productCacheService;
    private final ObjectMapper objectMapper;

    public List<Product> getAllProducts() {
        String cached = productCacheService.getCachedProductList();
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<Product>>() {});
            } catch (Exception e) {
                // fall through to fresh DB load
            }
        }

        List<Product> products = productRepository.findAll();
        try {
            productCacheService.cacheProductList(objectMapper.writeValueAsString(products));
        } catch (Exception e) {
            // ignore cache failures
        }
        return products;
    }

    public Product getProduct(Long productId) {
        String cached = productCacheService.getCachedProduct(productId);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, Product.class);
            } catch (Exception e) {
                // fall through to fresh DB load
            }
        }

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        try {
            productCacheService.cacheProduct(productId, objectMapper.writeValueAsString(product));
        } catch (Exception e) {
            // ignore cache failures
        }
        return product;
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findTop10ByAvailableStockLessThanOrderByAvailableStockAsc(10);
    }

    @Transactional
    public Product createProduct(Product product) {
        Product saved = productRepository.save(product);
        productCacheService.evictAll();
        return saved;
    }

    @Transactional
    public Product updateProduct(Long id, Product productUpdate) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        existing.setName(productUpdate.getName());
        existing.setPrice(productUpdate.getPrice());
        existing.setAvailableStock(productUpdate.getAvailableStock());
        Product saved = productRepository.save(existing);
        productCacheService.evictAll();
        productCacheService.evictProduct(id);
        return saved;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        productCacheService.evictProduct(id);
        productCacheService.evictAll();
    }
}
