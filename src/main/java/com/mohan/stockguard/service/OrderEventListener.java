package com.mohan.stockguard.service;

import com.mohan.stockguard.config.RabbitMqConfig;
import com.mohan.stockguard.dto.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventListener {

    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE)
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: {}", event);
        // Placeholder for async downstream work, e.g. sending confirmation email,
        // updating fulfillment systems, or triggering warehouse reservation.
    }
}
