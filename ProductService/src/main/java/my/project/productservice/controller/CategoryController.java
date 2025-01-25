package my.project.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.exception.ValidationErrorException;
import my.project.productservice.service.CategoryService;
import my.project.productservice.validator.CategoryValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllParentCategoriesWithSubcategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryDTO categoryDTO,
                                                      BindingResult bindingResult) {
        categoryValidator.validate(categoryDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        CategoryDTO created = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @RequestBody @Valid CategoryDTO categoryDTO,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }

        CategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {
        if (categoryService.deleteCategory(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
