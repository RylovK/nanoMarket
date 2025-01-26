package my.project.cartservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data transfer object (DTO) representing a request to update a shopping cart.
 * Contains the cart ID, product ID, and the quantity to be updated.
 *
 * @param cartId   the ID of the shopping cart to be updated
 * @param productId the ID of the product to be updated in the cart
 * @param quantity the new quantity of the product in the cart; must be positive
 */
public record CartUpdateRequest(
        @NotNull(message = "Cart ID cannot be null") Long cartId,
        @NotNull(message = "Product ID cannot be null") Long productId,
        @Positive(message = "Quantity cannot be negative") Integer quantity
) {}
