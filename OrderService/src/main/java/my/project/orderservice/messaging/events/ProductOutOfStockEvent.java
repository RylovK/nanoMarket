package my.project.orderservice.messaging.events;

import java.io.Serializable;
import java.util.UUID;

public record ProductOutOfStockEvent(UUID orderId,
                                     String reason
) implements Serializable {}