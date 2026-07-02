package com.mohan.stockguard.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class RateLimitServiceTest {

    @Autowired
    private RateLimitService rateLimitService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @Test
    void tryConsumeAllowsWithinLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(eq("rate:anonymous"))).thenReturn(1L);

        boolean allowed = rateLimitService.tryConsume("anonymous");

        assertThat(allowed).isTrue();
        Mockito.verify(redisTemplate).expire(eq("rate:anonymous"), eq(Duration.ofMinutes(1)));
    }
}
