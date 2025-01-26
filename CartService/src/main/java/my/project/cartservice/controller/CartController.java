package my.project.cartservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cart API", description = "API for managing shopping carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{cartId}")
    @Operation(
            summary = "Get a cart by ID",
            description = "Retrieve a shopping cart by its unique ID")
    public ResponseEntity<Cart> getCart(
            @PathVariable
            @Parameter(description = "The ID of the cart to retrieve") Long cartId) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @PostMapping("/update")
    @Operation(
            summary = "Update a cart",
            description = "Update the contents of a shopping cart based on the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the cart to update, including product IDs and quantities",
                    required = true))
    public ResponseEntity<String> updateCart(
            @RequestBody @Valid CartUpdateRequest request) {
        cartService.updateCart(request);
        return ResponseEntity.ok("Cart updated successfully");
    }

    @DeleteMapping("/{cartId}")
    @Operation(
            summary = "Clear a cart by ID",
            description = "Remove all items from the shopping cart with the specified ID")
    public ResponseEntity<String> clearCart(
            @PathVariable
            @Parameter(description = "The ID of the cart to clear") Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
