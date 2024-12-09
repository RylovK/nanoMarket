package my.project.productservice.service.impl;


import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.BrandDTO;
import my.project.productservice.entity.Brand;
import my.project.productservice.exception.BrandNotFoundException;
import my.project.productservice.mapper.BrandMapper;
import my.project.productservice.repository.BrandRepository;
import my.project.productservice.service.BrandService;
import org.springframework.data.domain.Page;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public Page<BrandDTO> getAllBrands(String brandName, Pageable pageable) {
        if (brandName == null) return brandRepository.findAll(pageable).map(brandMapper::toDto);
        Specification<Brand> specification = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("brandName")), "%" + brandName + "%");
        return brandRepository.findAll(specification, pageable).map(brandMapper::toDto);
    }

    @Override
    public BrandDTO getBrandById(long id) throws BrandNotFoundException {
        return brandRepository.findById(id)
                .map(brandMapper::toDto)
                .orElseThrow(BrandNotFoundException::new);
    }

    @Transactional
    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand saved = brandRepository.save(brandMapper.toEntity(brandDTO));
        return brandMapper.toDto(saved);
    }

    @Transactional
    @Override
    public BrandDTO updateBrand(long id, BrandDTO brandDTO) throws BrandNotFoundException {
        Optional<Brand> byId = brandRepository.findById(id);
        if (byId.isEmpty()) throw new BrandNotFoundException();
        Brand existing = byId.get();
        Brand updatedBrand = brandMapper.updateBrand(brandDTO, existing);
        Brand saved = brandRepository.save(updatedBrand);
        return brandMapper.toDto(saved);
    }

    @Transactional
    @Override
    public boolean deleteBrand(long id) {
        Optional<Brand> byId = brandRepository.findById(id);
        if (byId.isPresent()) {
            brandRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
