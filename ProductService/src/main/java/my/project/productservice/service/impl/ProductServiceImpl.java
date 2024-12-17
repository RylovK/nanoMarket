package my.project.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.ProductAvailabilityDTO;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.dto.ProductReservationRequest;
import my.project.productservice.entity.Brand;
import my.project.productservice.entity.Category;
import my.project.productservice.entity.Product;
import my.project.productservice.entity.ProductImage;
import my.project.productservice.exception.BrandNotFoundException;
import my.project.productservice.exception.CategoryNotFoundException;
import my.project.productservice.exception.OutOfStockException;
import my.project.productservice.exception.ProductNotFoundException;
import my.project.productservice.mapper.ProductMapper;
import my.project.productservice.repository.BrandRepository;
import my.project.productservice.repository.CategoryRepository;
import my.project.productservice.repository.ProductImageRepository;
import my.project.productservice.repository.ProductRepository;
import my.project.productservice.repository.specifications.ProductSpecification;
import my.project.productservice.service.FileUploadService;
import my.project.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final FileUploadService fileUploadService;
    private final ProductImageRepository productImageRepository;

    @Override
    public Page<ProductDTO> getAllProducts(Map<String, String> filters, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.getProductSpecification(filters);
        return productRepository.findAll(specification, pageable).map(productMapper::toProductDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toProductDTO).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Brand brand = brandRepository.findById(productDTO.getBrand().getId()).orElseThrow(BrandNotFoundException::new);
        Category category = categoryRepository.findById(productDTO.getCategory().getId()).orElseThrow(CategoryNotFoundException::new);
        Product product = productMapper.toProduct(productDTO);
        product.setBrand(brand);
        product.setCategory(category);
        Product saved = productRepository.save(product);
        return productMapper.toProductDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Brand brand = brandRepository.findById(productDTO.getBrand().getId()).orElseThrow(BrandNotFoundException::new);
        Category category = categoryRepository.findById(productDTO.getCategory().getId()).orElseThrow(CategoryNotFoundException::new);
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        Product updated = productMapper.updateProductDTO(productDTO, product);
        updated.setBrand(brand);
        updated.setCategory(category);
        Product saved = productRepository.save(updated);
        return productMapper.toProductDTO(saved);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> founded = productRepository.findById(id);
        if (founded.isPresent()) {
            productRepository.delete(founded.get());
            return true;
        }
        return false;
    }

    @Override
    public ProductAvailabilityDTO getProductAvailability(Long id) {
        return productMapper.toProductAvailabilityDTO(productRepository.findById(id).orElseThrow(ProductNotFoundException::new));
    }

    @Override
    @Transactional
    public void reserveProducts(List<ProductReservationRequest> reservationRequests) {
        for (ProductReservationRequest reservationRequest : reservationRequests) {
            System.out.println(reservationRequest.getProductId() + ": " + reservationRequest.getQuantity());
            Product product = productRepository.findById(reservationRequest.getProductId()).orElseThrow(ProductNotFoundException::new);
            if (product.getQuantity() < reservationRequest.getQuantity()) {
                throw new OutOfStockException("Product " + product.getName() + " is out of stock");
            }
            product.setQuantity(product.getQuantity() - reservationRequest.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    @Transactional
    public ProductDTO uploadImage(Long id, MultipartFile file) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        String url = fileUploadService.uploadFile(file, this.getClass().getSimpleName());
        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(url);
        productImage.setProduct(product);
        product.getImages().add(productImage);
        productImageRepository.save(productImage);
        return productMapper.toProductDTO(productRepository.save(product));
    }
}
