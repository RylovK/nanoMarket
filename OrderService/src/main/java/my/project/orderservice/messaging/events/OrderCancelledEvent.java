package my.project.orderservice.messaging.events;

import java.io.Serializable;
import java.util.UUID;

public record OrderCancelledEvent(UUID orderId,
                                  Long customerId,
                                  String reason
) implements Serializable {}
