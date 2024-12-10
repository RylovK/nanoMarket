package my.project.productservice.mapper;

import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);

    Category updateCategory(CategoryDTO categoryDTO, @MappingTarget Category category);

    List<CategoryDTO> categoriesToDTOs(List<Category> categories);

    default List<CategoryDTO> mapSubcategories(Set<Category> subcategories) {
        return subcategories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
