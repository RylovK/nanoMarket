package my.project.orderservice.dto;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductAvailabilityDTO {

    private Long id;

    private BigDecimal price;

    private Integer quantity;
}


