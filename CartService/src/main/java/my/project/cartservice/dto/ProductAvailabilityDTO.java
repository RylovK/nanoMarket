package my.project.cartservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Data transfer object (DTO) representing the availability of a product.
 * Contains details about the product's unique identifier and available quantity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductAvailabilityDTO (Long id, Integer quantity) {}
