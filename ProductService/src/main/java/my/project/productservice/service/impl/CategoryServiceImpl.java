package my.project.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.entity.Category;
import my.project.productservice.exception.CategoryNotFoundException;
import my.project.productservice.repository.CategoryRepository;
import my.project.productservice.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllParentCategoriesWithSubcategories() {
        List<Category> allParentCategories = categoryRepository.findAllByParentIsNull();
        return null;
    }

    @Override
    public CategoryDTO getCategoryById(Long id) throws CategoryNotFoundException {
        return null;
    }

    @Transactional
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        return null;
    }

    @Transactional
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException {
        return null;
    }

    @Transactional
    @Override
    public boolean deleteCategory(Long id) {
        return false;
    }
}
