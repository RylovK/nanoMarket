package my.project.cartservice.service;

import lombok.RequiredArgsConstructor;
import my.project.cartservice.dto.CartUpdateRequest;
import my.project.cartservice.entity.Cart;
import my.project.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setLastUpdated(LocalDateTime.now());
            return cart;
        });
    }

    public void updateCart(CartUpdateRequest request) {
        Cart cart = getCart(request.getCartId());

        if (request.getQuantity() > 0) {
            cart.getItems().put(request.getProductId(), request.getQuantity());
        } else {
            cart.getItems().remove(request.getProductId());
        }

        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public void clearCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
