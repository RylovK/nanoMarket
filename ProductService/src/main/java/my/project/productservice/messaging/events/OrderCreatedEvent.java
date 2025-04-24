package my.project.productservice.messaging.events;

import my.project.productservice.dto.ProductReservationRequest;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId,
                                List<ProductReservationRequest> items
) {}