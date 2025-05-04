package my.project.events;

import java.io.Serializable;
import java.util.UUID;

public record ProductOutOfStockEvent(UUID orderId,
                                     String reason
) implements Serializable {}