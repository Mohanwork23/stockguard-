package com.mohan.stockguard.service;

import com.mohan.stockguard.config.RabbitMqConfig;
import com.mohan.stockguard.dto.OrderEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

@SpringBootTest
@Testcontainers
class OrderEventIntegrationTest {

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.11-management");

    @DynamicPropertySource
    static void registerRabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbit.getAmqpPort());
        registry.add("spring.rabbitmq.username", () -> rabbit.getAdminUsername());
        registry.add("spring.rabbitmq.password", () -> rabbit.getAdminPassword());
    }

    @Autowired
    private OrderEventPublisher publisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void publishesOrderEventToRabbitMq() throws Exception {
        OrderEvent event = new OrderEvent(null, 42L, 100L, 1, "CONFIRMED", Instant.now());

        publisher.publish(event);

        OrderEvent received = null;
        for (int i = 0; i < 50; i++) {
            Object obj = rabbitTemplate.receiveAndConvert(RabbitMqConfig.ORDER_QUEUE);
            if (obj instanceof OrderEvent) {
                received = (OrderEvent) obj;
                break;
            }
            Thread.sleep(100);
        }

        Assertions.assertNotNull(received, "Expected to receive an OrderEvent from RabbitMQ");
        Assertions.assertEquals(event.getUserId(), received.getUserId());
        Assertions.assertEquals(event.getProductId(), received.getProductId());
    }
}
