package my.project.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class BrandDTO {

    private long id;

    private String name;

    private Set<ProductDTO> products = new HashSet<>();
}
