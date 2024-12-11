package my.project.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.entity.Category;
import my.project.productservice.exception.CategoryNotFoundException;
import my.project.productservice.mapper.CategoryMapper;
import my.project.productservice.repository.CategoryRepository;
import my.project.productservice.service.CategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable("categoryListCache")
    public List<CategoryDTO> getAllParentCategoriesWithSubcategories() {
        List<Category> allParentCategories = categoryRepository.findAllByParentIsNull();
        return allParentCategories.stream().map(categoryMapper::toDTO).toList();

    }

    @Override
    public CategoryDTO getCategoryById(Long id) throws CategoryNotFoundException {
        return categoryMapper.toDTO(categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new));
    }

    @Transactional
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating category: {} with parent: {}", categoryDTO.getName(), categoryDTO.getParentId());
        Category category = categoryMapper.toEntity(categoryDTO);

        if (categoryDTO.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentId()).orElseThrow(CategoryNotFoundException::new);
            category.setParent(parentCategory);
            log.info("Category mapped: {} with parent: {}", category.getName(), category.getParent().getId());
        }
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        Category updated = categoryMapper.updateCategory(categoryDTO, category);
        return categoryMapper.toDTO(updated);
    }

    @Transactional
    @Override
    public boolean deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Category categoryToDelete = category.get();
            categoryRepository.deleteAll(categoryToDelete.getSubcategories());
            categoryRepository.delete(categoryToDelete);
            return true;
        }
        return false;
    }
}
