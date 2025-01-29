package my.project.productservice.repository;

import my.project.productservice.dto.ProductAvailabilityDTO;
import my.project.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    Optional<Product> findByNameAndDescription(String name, String description);

    Optional<ProductAvailabilityDTO> findAvailabilityById(Long id);
}
