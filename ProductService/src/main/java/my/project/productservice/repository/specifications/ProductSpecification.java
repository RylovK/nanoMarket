package my.project.productservice.repository.specifications;

import jakarta.persistence.criteria.Predicate;
import my.project.productservice.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductSpecification {
    /*
     *                    <li>"name" - Product name or description for filtering.</li>
     *                    <li>"categoryId" - Category ID for filtering products by their category.</li>
     *                    <li>"brandId" - Brand ID for filtering products by their brand.</li>

     */
    public static Specification<Product> getProductSpecification(Map<String, String> filters) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toLowerCase();
                switch (key) {
                    case "name" -> {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + value + "%"));
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + value + "%"));
                    }
                    case "categoryId" ->
                            predicates.add(criteriaBuilder.equal(root.get("category").get("id"), Long.parseLong(value)));
                    case "brandId" ->
                            predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), Long.parseLong(value)));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

