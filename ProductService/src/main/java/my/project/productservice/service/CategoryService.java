package my.project.productservice.service;

import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.exception.CategoryNotFoundException;

import java.util.List;

/**
 * Service interface for managing categories.
 */
public interface CategoryService {

    /**
     * Retrieves all parent categories along with their subcategories.
     * Parent categories are those that do not have a parent category.
     * Each parent category will include its direct subcategories in the result.
     * @return a list of {@link CategoryDTO} representing parent categories with their subcategories
     */
    List<CategoryDTO> getAllParentCategoriesWithSubcategories();

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id the unique ID of the category
     * @return the {@link CategoryDTO} with the given ID
     * @throws CategoryNotFoundException if the category with the given ID is not found
     */
    CategoryDTO getCategoryById(Long id) throws CategoryNotFoundException;

    /**
     * Creates a new category.
     *
     * @param categoryDTO the {@link CategoryDTO} containing category details to be created
     * @return the created {@link CategoryDTO}
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * Updates an existing category.
     *
     * @param id the unique ID of the category to be updated
     * @param categoryDTO the {@link CategoryDTO} containing updated category details
     * @return the updated {@link CategoryDTO}
     * @throws CategoryNotFoundException if the category to be updated does not exist
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException;

    /**
     * Deletes a category by its unique identifier.
     *
     * @param id the unique ID of the category to be deleted
     * @return true if the category was successfully deleted, false if no category with the given ID was found
     */
    boolean deleteCategory(Long id);
}
