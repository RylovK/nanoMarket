package my.project.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import my.project.productservice.entity.Brand;
import my.project.productservice.entity.Category;
import my.project.productservice.entity.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Getter @Setter
public class ProductDTO {

    private long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 1, max = 25, message = "Product name must be between 1 and 25 symbols")
    private String name;

    @NotBlank(message = "Product description cannot be blank")
    @Size(min = 10, max = 255, message = "Product description must be between 10 and 255 symbols")
    private String description;

    @NotNull(message = "A product must have a price")
    private BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be 0 or more")
    private int quantity;

    private Category category;

    private Brand brand;

    @URL(message = "Invalid URL format")
    private ProductImage image;
}
