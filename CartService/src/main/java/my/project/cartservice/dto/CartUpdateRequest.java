package my.project.cartservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartUpdateRequest {

    @NotNull(message = "Cart ID cannot be null")
    private Long cartId; // ID корзины

    @NotNull(message = "Product ID cannot be null")
    private Long productId; // ID продукта

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity; // Количество товара
}