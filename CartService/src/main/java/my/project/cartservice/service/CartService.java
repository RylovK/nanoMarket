package my.project.cartservice.service;

import my.project.cartservice.dto.CartUpdateRequest;
import my.project.cartservice.entity.Cart;
/**
 * Service interface for managing shopping cart operations.
 * Provides methods to retrieve, update, and clear a cart.
 */
public interface CartService {

    /**
     * Retrieves the shopping cart with the specified ID.
     *
     * @param cartId the unique identifier of the cart
     * @return the {@link Cart} object representing the shopping cart
     */
    Cart getCart(Long cartId);

    /**
     * Updates the shopping cart with the provided details.
     *
     * @param request the {@link CartUpdateRequest} object containing update details
     */
    void updateCart(CartUpdateRequest request);

    /**
     * Clears all items from the shopping cart with the specified ID.
     *
     * @param cartId the unique identifier of the cart to be cleared
     */
    void clearCart(Long cartId);
}

