package my.project.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductAvailabilityDTO {

    private Long id;

    private BigDecimal price;

    private Integer quantity;
}
