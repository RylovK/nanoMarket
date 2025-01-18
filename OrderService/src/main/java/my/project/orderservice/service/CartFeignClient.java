package my.project.orderservice.service;

import my.project.orderservice.dto.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for interacting with the Cart Service.
 * This client provides methods to retrieve and clear a cart by its ID.
 */
@FeignClient(name = "cart-service")
public interface CartFeignClient {

    /**
     * Feign client for interacting with the Cart Service.
     * This client provides methods to retrieve and clear a cart by its ID.
     */
    @GetMapping("/api/v1/carts/{cartId}")
    Cart getCart(@PathVariable("cartId") Long cartId);

    /**
     * Clears a cart by its unique identifier.
     * This operation typically removes all items from the cart.
     *
     * @param cartId the unique identifier of the cart to clear
     */
    @DeleteMapping("/api/v1/carts/{cartId}")
    void clearCart(@PathVariable("cartId") Long cartId);
}
