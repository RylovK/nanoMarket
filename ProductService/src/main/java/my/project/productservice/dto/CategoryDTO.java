package my.project.productservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 25, message = "Category name must be between 2 and 25 symbols")
    private String name;

    @NotEmpty(message = "Category description cannot be empty")
    @Size(min = 10, max = 255, message = "Category description must be between 10 and 255 symbols")
    private String description;

    private Long parentId;

    private Set<CategoryDTO> subcategories = new HashSet<>();

    //private Set<ProductDTO> products = new HashSet<>(); TODO: нужен ли список продуктов?
}
