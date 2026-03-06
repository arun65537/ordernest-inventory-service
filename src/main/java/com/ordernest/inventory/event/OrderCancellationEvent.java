package com.ordernest.inventory.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCancellationEvent(
        UUID productId,
        Integer quantity,
        String orderId,
        OrderCancellationEventType eventType,
        String reason,
        Instant timestamp
) {
}
