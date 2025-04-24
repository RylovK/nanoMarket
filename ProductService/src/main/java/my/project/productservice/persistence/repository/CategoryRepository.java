package my.project.productservice.persistence.repository;

import my.project.productservice.persistence.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "subcategories")
    List<Category> findAllByParentIsNull();

    List<Category> findCategoriesByNameContaining(String name);

    Optional<Category> findByName(String name);
}
