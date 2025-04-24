package my.project.cartservice.service;

import my.project.cartservice.entity.Cart;
import my.project.cartservice.entity.CartItem;

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


    void updateCart(Long customerId, CartItem cartItem);

    /**
     * Clears all items from the shopping cart with the specified ID.
     *
     * @param cartId the unique identifier of the cart to be cleared
     */
    void clearCart(Long cartId);
}

