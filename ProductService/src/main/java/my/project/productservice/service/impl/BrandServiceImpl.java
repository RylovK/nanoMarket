package my.project.productservice.service.impl;


import my.project.productservice.dto.BrandDTO;
import my.project.productservice.exception.BrandNotFoundException;
import my.project.productservice.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    @Override
    public Page<BrandDTO> getAllBrands(Pageable pageable, String brandName) {
        return null;
    }

    @Override
    public BrandDTO getBrandById(int id) throws BrandNotFoundException {
        return null;
    }

    @Transactional
    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) {
        return null;
    }

    @Transactional
    @Override
    public BrandDTO updateBrand(BrandDTO brandDTO) throws BrandNotFoundException {
        return null;
    }

    @Transactional
    @Override
    public boolean deleteBrand(int id) {
        return false;
    }
}
