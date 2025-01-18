package my.project.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import my.project.productservice.entity.ProductImage;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;
/**
 * Data transfer object (DTO) representing a product.
 * Contains details such as the product's name, description, price, quantity,
 * associated category, brand, and images.
 */
@Getter @Setter
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 1, max = 25, message = "Product name must be between 1 and 25 symbols")
    private String name;

    @NotBlank(message = "Product description cannot be blank")
    @Size(min = 10, max = 255, message = "Product description must be between 10 and 255 symbols")
    private String description;

    @NotNull(message = "A product must have a price")
    private BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be 0 or more")
    private Integer quantity;

    private CategoryDTO category;

    private BrandDTO brand;

    private List<String> images;
}
