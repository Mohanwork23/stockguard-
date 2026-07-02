package com.mohan.stockguard.service;

import com.mohan.stockguard.config.RabbitMqConfig;
import com.mohan.stockguard.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(OrderEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.ORDER_EXCHANGE,
            RabbitMqConfig.ORDER_ROUTING_KEY,
            event
        );
    }
}
