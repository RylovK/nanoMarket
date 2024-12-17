package my.project.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductAvailabilityDTO {

    private Long id;

    private String name;

    private Integer quantity;
}
