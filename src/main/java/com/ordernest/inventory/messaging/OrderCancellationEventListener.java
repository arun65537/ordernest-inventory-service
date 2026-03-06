package com.ordernest.inventory.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordernest.inventory.event.OrderCancellationEvent;
import com.ordernest.inventory.event.OrderCancellationEventType;
import com.ordernest.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancellationEventListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-cancelled-events}",
            groupId = "${app.kafka.consumer.order-cancelled-group-id}"
    )
    public void onOrderCancelledEvent(String payload) {
        try {
            OrderCancellationEvent cancellationEvent = objectMapper.readValue(payload, OrderCancellationEvent.class);
            if (cancellationEvent.eventType() != OrderCancellationEventType.CANCALLED) {
                return;
            }

            productService.releaseProductStock(cancellationEvent.productId(), cancellationEvent.quantity());
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse order cancellation event payload: {}", payload, ex);
        } catch (Exception ex) {
            log.error("Failed to process order cancellation event payload: {}", payload, ex);
        }
    }
}
