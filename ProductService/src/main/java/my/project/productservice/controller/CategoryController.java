package my.project.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Category API", description = "API for category management")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;

    @GetMapping
    @Operation(summary = "Get all categories",
            description = "Retrieve a list of all parent categories with their subcategories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllParentCategoriesWithSubcategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID",
            description = "Retrieve details of a category by its unique ID")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable
            @Parameter(description = "ID of the category to retrieve", required = true) Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create a new category",
            description = "Create a new category with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category details to create",
                    required = true,
                    content = @Content(mediaType = "application/json")
            ))
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
    @Operation(summary = "Update an existing category",
            description = "Update the details of an existing category",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated category details",
                    required = true,
                    content = @Content(mediaType = "application/json")
            ))
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable
            @Parameter(description = "ID of the category to update", required = true) Long id,

            @RequestBody @Valid CategoryDTO categoryDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }

        CategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID",
            description = "Delete the category with the specified ID")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {
        if (categoryService.deleteCategory(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
