package my.project.orderservice.dto;

import jakarta.validation.constraints.Min;

public record ProductReservationRequest (Long productId, @Min(1) Integer quantity) {}