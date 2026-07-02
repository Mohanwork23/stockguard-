package com.mohan.stockguard.service;

import com.mohan.stockguard.config.RabbitMqConfig;
import com.mohan.stockguard.dto.OrderEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderEventPublisherTest {

    @Autowired
    private OrderEventPublisher publisher;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void publishesOrderEventToExchange() {
        OrderEvent event = new OrderEvent(1L, 2L, 3L, 1, "CONFIRMED", null);

        publisher.publish(event);

        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        Mockito.verify(rabbitTemplate).convertAndSend(
            Mockito.eq(RabbitMqConfig.ORDER_EXCHANGE),
            Mockito.eq(RabbitMqConfig.ORDER_ROUTING_KEY),
            captor.capture());

        assertThat(captor.getValue()).isEqualTo(event);
    }
}
