package my.project.productservice.validator;

import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.entity.Category;
import my.project.productservice.repository.CategoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryValidator implements Validator {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Category.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryDTO category = (CategoryDTO) target;
        Optional<Category> founded = categoryRepository.findByName(category.getName());
        if (founded.isPresent()) {
            errors.rejectValue("name", "duplicate", "Category name already exists");
        }
    }
}
