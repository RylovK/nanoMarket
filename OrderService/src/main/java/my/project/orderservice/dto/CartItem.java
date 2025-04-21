package my.project.orderservice.dto;

import java.math.BigDecimal;

public record CartItem (
    Long productId,
    Integer quantity,
    BigDecimal price)
{}
