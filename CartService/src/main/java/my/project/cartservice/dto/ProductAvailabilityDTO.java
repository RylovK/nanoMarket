package my.project.cartservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Data transfer object (DTO) representing the availability of a product.
 * Contains details about the product's unique identifier, available quantity and price
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductAvailabilityDTO (Long id, Integer quantity, BigDecimal price) {}
