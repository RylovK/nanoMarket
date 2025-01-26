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
        Long productId = request.productId();
        ProductAvailabilityDTO product = productFeignClient.getProductAvailability(productId);

        if (product == null) {
            log.error("Product not found");
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        if (request.quantity() > product.quantity()) {
            log.warn("Quantity is greater than product stock");
            throw new IllegalArgumentException("Not enough stock for product: " + productId);
        }

        Cart cart = getCart(request.cartId());

        if (request.quantity() > 0) {
            cart.getItems().put(productId, request.quantity());
        } else {
            cart.getItems().remove(productId);
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
