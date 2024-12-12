package my.project.productservice.validator;

import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.entity.Product;
import my.project.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductValidator implements Validator {

    private final ProductRepository productRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Product.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductDTO productDTO = (ProductDTO) target;
        Optional<Product> founded = productRepository.findByNameAndDescription(productDTO.getName(), productDTO.getDescription());
        if (founded.isPresent()) {
            errors.rejectValue("description", "duplicate", "Product with this name and description already exists");
        }
    }
}
