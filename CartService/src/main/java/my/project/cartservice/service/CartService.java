package my.project.cartservice.service;

import my.project.cartservice.dto.CartUpdateRequest;
import my.project.cartservice.entity.Cart;

public interface CartService {

    Cart getCart(Long cartId);

    void updateCart(CartUpdateRequest request);

    void clearCart(Long cartId);
}
