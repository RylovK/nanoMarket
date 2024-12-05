package my.project.productservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
public class CategoryDTO {

    private long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 25, message = "Category name must be between 2 and 25 symbols")
    private String name;

    @NotEmpty(message = "Category description cannot be empty")
    @Size(min = 10, max = 255, message = "Category description must be between 10 and 255 symbols")
    private String description;

    private long parentId;

    private List<CategoryDTO> subcategories = new ArrayList<>();

    private Set<ProductDTO> products = new HashSet<>();
}
