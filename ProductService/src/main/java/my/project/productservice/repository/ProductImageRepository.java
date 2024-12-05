package my.project.productservice.repository;

import my.project.productservice.entity.ProductImage;
import org.springframework.data.repository.CrudRepository;

public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
}
