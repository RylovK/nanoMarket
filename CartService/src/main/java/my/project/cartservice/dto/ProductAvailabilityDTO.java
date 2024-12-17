package my.project.cartservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductAvailabilityDTO {

    private Long id;

    private Integer quantity;

}
