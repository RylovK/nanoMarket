package my.project.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO (UUID itemId, Long productId, BigDecimal price, Integer quantity) {}
