package my.project.cartservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
/**
 * Data transfer object (DTO) representing a request to update a shopping cart.
 * Contains the cart ID, product ID, and the quantity to be updated.
 */
@Getter @Setter
public class CartUpdateRequest {

    @NotNull(message = "Cart ID cannot be null")
    private Long cartId;

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}