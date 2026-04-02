package com.ordernest.inventory.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordernest.inventory.event.OrderStatusChangedEvent;
import com.ordernest.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusChangedEventListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-status-events:order.status.events}",
            groupId = "${app.kafka.consumer.order-status-group-id:ordernest-inventory-service-order-status-consumer}"
    )
    public void onOrderStatusChangedEvent(String payload) {
        try {
            OrderStatusChangedEvent event = objectMapper.readValue(payload, OrderStatusChangedEvent.class);
            if (!"CANCELLED".equalsIgnoreCase(event.currentStatus())) {
                return;
            }
            if ("CANCELLED".equalsIgnoreCase(event.previousStatus())) {
                return;
            }

            productService.releaseProductStock(event.productId(), event.quantity());
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse order status changed event payload: {}", payload, ex);
        } catch (Exception ex) {
            log.error("Failed to process order status changed event payload: {}", payload, ex);
        }
    }
}
