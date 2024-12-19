package my.project.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.cartservice.dto.CartUpdateRequest;
import my.project.cartservice.dto.ProductAvailabilityDTO;
import my.project.cartservice.entity.Cart;
import my.project.cartservice.repository.CartRepository;
import my.project.cartservice.service.CartService;
import my.project.cartservice.service.ProductFeignClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductFeignClient productFeignClient;

    public Cart getCart(Long cartId) {
        log.info("Getting cart with id: {}", cartId);
        return cartRepository.findById(cartId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setId(cartId);
            return cart;
        });
    }

    public void updateCart(CartUpdateRequest request) {
        ProductAvailabilityDTO product = productFeignClient.getProductAvailability(request.getProductId());

        if (product == null) {
            log.error("Product not found");
            throw new IllegalArgumentException("Product not found: " + request.getProductId());
        }
        if (request.getQuantity() > product.getQuantity()) {
            log.warn("Quantity is greater than product quantity");
            throw new IllegalArgumentException("Not enough stock for product: " + request.getProductId());
        }

        Cart cart = getCart(request.getCartId());

        if (request.getQuantity() > 0) {
            cart.getItems().put(request.getProductId(), request.getQuantity());
        } else {
            cart.getItems().remove(request.getProductId());
        }

        cart.setLastUpdated(LocalDateTime.now());
        log.info("Updating cart {}", cart);
        cartRepository.save(cart);
    }

    public void clearCart(Long cartId) {
        log.info("Clearing cart {}", cartId);
        cartRepository.deleteById(cartId);
    }
}
