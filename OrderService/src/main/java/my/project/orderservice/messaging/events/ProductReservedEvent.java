package my.project.orderservice.messaging.events;

import java.io.Serializable;
import java.util.UUID;

public record ProductReservedEvent(
        UUID orderId
) implements Serializable {}
