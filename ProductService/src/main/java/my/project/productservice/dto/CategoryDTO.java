package my.project.productservice.dto;


import java.util.HashSet;
import java.util.Set;

public class CategoryDTO {

    private long id;

    private String name;

    private String description;

    private long parentId;

    private Set<ProductDTO> products = new HashSet<>();
}
