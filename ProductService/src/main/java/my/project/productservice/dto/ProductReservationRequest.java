package my.project.productservice.dto;

import jakarta.validation.constraints.Min;

/**
 * Data transfer object (DTO) representing a request to reserve a product.
 * Contains the product ID and the quantity to be reserved.
 */
public record ProductReservationRequest (Long productId, @Min(1) Integer quantity) {}
