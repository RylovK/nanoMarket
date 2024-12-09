package my.project.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class BrandDTO {

    private Long id;

    @NotBlank(message = "Brand name cannot be blank")
    @Size(min = 1, max = 50, message = "Brand name must be between 1 and 50 symbols")
    private String brandName;

    //@URL(message = "Invalid URL format")
    private String logoUrl;

//    private Set<ProductDTO> products = new HashSet<>();
}
