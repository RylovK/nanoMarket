package my.project.orderservice.dto;

import java.math.BigDecimal;

public record ProductAvailabilityDTO(Long id, BigDecimal price, Integer quantity) {}


