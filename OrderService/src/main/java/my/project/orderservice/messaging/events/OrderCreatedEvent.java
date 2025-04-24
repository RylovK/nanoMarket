package my.project.orderservice.messaging.events;

import my.project.orderservice.dto.OrderItemDTO;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId,
                                List<OrderItemDTO> items
) implements Serializable {}
