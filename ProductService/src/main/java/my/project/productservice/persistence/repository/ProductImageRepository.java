package my.project.productservice.persistence.repository;

import my.project.productservice.persistence.entity.ProductImage;
import org.springframework.data.repository.CrudRepository;

public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
}
