package com.mohan.stockguard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    private static final String PRODUCTS_CACHE_KEY = "product:all";

    private final StringRedisTemplate redisTemplate;

    public void evictProduct(Long productId) {
        redisTemplate.delete(getProductKey(productId));
        redisTemplate.delete(PRODUCTS_CACHE_KEY);
    }

    public void evictAll() {
        redisTemplate.delete(PRODUCTS_CACHE_KEY);
    }

    public String getCachedProduct(Long productId) {
        return redisTemplate.opsForValue().get(getProductKey(productId));
    }

    public String getCachedProductList() {
        return redisTemplate.opsForValue().get(PRODUCTS_CACHE_KEY);
    }

    public void cacheProduct(Long productId, String payload) {
        redisTemplate.opsForValue().set(getProductKey(productId), payload, CACHE_TTL);
    }

    public void cacheProductList(String payload) {
        redisTemplate.opsForValue().set(PRODUCTS_CACHE_KEY, payload, CACHE_TTL);
    }

    private String getProductKey(Long productId) {
        return "product:" + productId;
    }
}
