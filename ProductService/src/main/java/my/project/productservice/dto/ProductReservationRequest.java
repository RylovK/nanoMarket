package my.project.productservice.dto;

import java.math.BigDecimal;

public record ProductReservationRequest (Long productId, BigDecimal price, Integer quantity) {}
