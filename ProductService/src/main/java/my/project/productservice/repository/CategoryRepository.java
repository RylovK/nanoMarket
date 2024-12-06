package my.project.productservice.repository;

import my.project.productservice.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "subcategories")
    List<Category> findAllByParentIsNull();

    List<Category> findCategoriesByNameContaining(String name);
}
