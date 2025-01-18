package my.project.productservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Data transfer object (DTO) representing a product category.
 * Contains information about the category's name, description, parent category,
 * and subcategories.
 */
@Getter
@Setter
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 25, message = "Category name must be between 2 and 25 symbols")
    private String name;

    @NotEmpty(message = "Category description cannot be empty")
    @Size(min = 10, max = 255, message = "Category description must be between 10 and 255 symbols")
    private String description;

    /**
     * The unique identifier of the parent category.
     * If the category has no parent, this field may be {@code null}.
     */
    private Long parentId;

    /**
     * A set of subcategories under this category.
     * This field contains child categories related to this category.
     */
    private Set<CategoryDTO> subcategories = new HashSet<>();

    //private Set<ProductDTO> products = new HashSet<>(); TODO: Should the list of products be included?
}