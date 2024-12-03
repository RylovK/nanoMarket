package my.project.productservice.dto;

import my.project.productservice.entity.Brand;
import my.project.productservice.entity.Category;
import my.project.productservice.entity.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductDTO {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private int quantity;

    private Category category;

    private Brand brand;

    private ProductImage image;
}
