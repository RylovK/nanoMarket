package my.project.productservice.dto;

import java.util.HashSet;
import java.util.Set;

public class BrandDTO {

    private long id;

    private String name;

    private Set<ProductDTO> products = new HashSet<>();
}
