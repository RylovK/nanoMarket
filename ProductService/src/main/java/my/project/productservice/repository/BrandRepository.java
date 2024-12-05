package my.project.productservice.repository;

import my.project.productservice.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Page<Brand> findByBrandName(String brandName, Pageable pageable);
}
