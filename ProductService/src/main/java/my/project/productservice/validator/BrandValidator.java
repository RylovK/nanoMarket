package my.project.productservice.validator;

import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.BrandDTO;
import my.project.productservice.persistence.entity.Brand;
import my.project.productservice.persistence.repository.BrandRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BrandValidator implements Validator {

    private final BrandRepository brandRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Brand.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BrandDTO brandDTO = (BrandDTO) target;
        Optional<Brand> byId = brandRepository.findByBrandName(brandDTO.brandName());
        if (byId.isPresent()) {
            errors.rejectValue("brandName", "duplicate", "Brand with this name already exists");
        }
    }
}
