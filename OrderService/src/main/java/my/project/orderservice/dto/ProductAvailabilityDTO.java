package my.project.orderservice.dto;

import java.math.BigDecimal;

/**
 * Data transfer object (DTO) representing the availability of a product.
 * Contains details about the product's price and available quantity.
 */
public record ProductAvailabilityDTO(Long id, BigDecimal price, Integer quantity) {}


