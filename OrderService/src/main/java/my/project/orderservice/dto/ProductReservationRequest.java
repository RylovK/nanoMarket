package my.project.orderservice.dto;

import jakarta.validation.constraints.Min;

/**
 * A Data Transfer Object (DTO) representing a request to reserve a product.
 * This record contains the necessary information for reserving a specific
 * product, including its ID and the quantity to reserve.
 *
 * @param productId the unique identifier of the product to reserve
 * @param quantity  the quantity of the product to reserve; must be at least 1
 */
public record ProductReservationRequest (Long productId, @Min(1) Integer quantity) {}