package my.project.productservice.mapper;

import my.project.productservice.dto.CategoryDTO;
import my.project.productservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    CategoryDTO toDTO(Category category);

    //@Mapping(target = "parent", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(target = "id", ignore = true)
    Category updateCategory(CategoryDTO categoryDTO, @MappingTarget Category category);

    //List<CategoryDTO> categoriesToDTOs(List<Category> categories);

    default Set<CategoryDTO> mapSubcategories(Set<Category> subcategories) {
        return subcategories.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

//    default Category mapParent(Long parentId) {
//        if (parentId == null) {
//            return null;
//        }
//        Category parentCategory = new Category();
//        parentCategory.setId(parentId);
//        return parentCategory;
//    }
}
