package my.project.productservice.persistence.repository;

import my.project.productservice.persistence.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {

    //Page<Brand> findAll(Specification<Brand> brandName, Pageable pageable);

    Optional<Brand> findByBrandName(String brandName);
}
