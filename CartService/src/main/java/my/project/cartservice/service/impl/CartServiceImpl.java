package my.project.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.cartservice.dto.ProductAvailabilityDTO;
import my.project.cartservice.entity.Cart;
import my.project.cartservice.entity.CartItem;
import my.project.cartservice.repository.CartRepository;
import my.project.cartservice.service.CartService;
import my.project.cartservice.service.ProductFeignClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductFeignClient productFeignClient;

    public Cart getCart(Long customerId) {
        log.info("Getting cart with id: {}", customerId);
        return cartRepository.findById(customerId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setId(customerId);
            return cart;
        });
    }


    public void updateCart(Long customerId, CartItem cartItem) {
        Cart cart = getCart(customerId);
        Set< CartItem> items = cart.getItems();

        Optional<CartItem> existingItemOpt = items.stream()
                .filter(i -> Objects.equals(i.getProductId(), cartItem.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(cartItem.getQuantity());
        } else {
            Long productId = cartItem.getProductId();
            BigDecimal price = validateAndGetPrice(productId, cartItem.getQuantity());
            cartItem.setPrice(price);
            items.add(cartItem);
        }

        cart.setLastUpdated(LocalDateTime.now());
        log.info("Updating cart {}", cart);
        cartRepository.save(cart);
    }

    public void clearCart(Long cartId) {
        log.info("Clearing cart {}", cartId);
        cartRepository.deleteById(cartId);
    }

    private BigDecimal validateAndGetPrice(Long productId, int quantity) {
        ProductAvailabilityDTO product = productFeignClient.getProductAvailability(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        if (quantity > product.quantity()) {
            throw new IllegalArgumentException("Not enough stock for product: " + productId);
        }
        return product.price();
    }

}
