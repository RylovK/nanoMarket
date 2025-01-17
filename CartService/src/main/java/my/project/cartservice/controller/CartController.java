package my.project.cartservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.cartservice.dto.CartUpdateRequest;
import my.project.cartservice.entity.Cart;
import my.project.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCart(@Valid @RequestBody CartUpdateRequest request) {
        cartService.updateCart(request);
        return ResponseEntity.ok("Cart updated successfully");
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}