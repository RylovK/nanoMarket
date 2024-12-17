package my.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class OrderItemDTO {

    private UUID itemId;
    private Long productId;
    private BigDecimal price;
    private Integer quantity;
}
