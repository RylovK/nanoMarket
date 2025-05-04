package my.project.events;

import java.io.Serializable;
import java.util.UUID;

public record ProductReservedEvent(
        UUID orderId
) implements Serializable {}
