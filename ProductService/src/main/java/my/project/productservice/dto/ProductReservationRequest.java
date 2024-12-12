package my.project.productservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductReservationRequest {

    private Long productId;

    @Min(value = 1)
    private Integer quantity;
}
