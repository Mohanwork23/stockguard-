package com.mohan.stockguard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final int LIMIT = 20;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate redisTemplate;

    public boolean tryConsume(String key) {
        String redisKey = "rate:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count == null) {
            return false;
        }
        if (count == 1) {
            redisTemplate.expire(redisKey, WINDOW);
        }
        return count <= LIMIT;
    }
}
