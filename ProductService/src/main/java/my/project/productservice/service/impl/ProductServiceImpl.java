package my.project.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.dto.OrderItemDTO;
import my.project.productservice.dto.ProductAvailabilityDTO;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.persistence.entity.Brand;
import my.project.productservice.persistence.entity.Category;
import my.project.productservice.persistence.entity.Product;
import my.project.productservice.persistence.entity.ProductImage;
import my.project.productservice.exception.BrandNotFoundException;
import my.project.productservice.exception.CategoryNotFoundException;
import my.project.productservice.exception.ProductNotFoundException;
import my.project.productservice.mapper.ProductMapper;
import my.project.productservice.persistence.repository.BrandRepository;
import my.project.productservice.persistence.repository.CategoryRepository;
import my.project.productservice.persistence.repository.ProductImageRepository;
import my.project.productservice.persistence.repository.ProductRepository;
import my.project.productservice.persistence.repository.specifications.ProductSpecification;
import my.project.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;

    @Override
    public Page<ProductDTO> getAllProducts(Map<String, String> filters, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.getProductSpecification(filters);
        log.debug("Getting all products");
        return productRepository.findAll(specification, pageable).map(productMapper::toProductDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.debug("Getting product by id {}", id);
        return productRepository.findById(id).map(productMapper::toProductDTO).orElseThrow(ProductNotFoundException::new);
    }


    @Transactional
    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Brand brand = brandRepository.findById(productDTO.getBrand().id()).orElseThrow(BrandNotFoundException::new);
        Category category = categoryRepository.findById(productDTO.getCategory().getId()).orElseThrow(CategoryNotFoundException::new);
        Product product = productMapper.toProduct(productDTO);
        product.setBrand(brand);
        product.setCategory(category);
        Product saved = productRepository.save(product);
        log.info("Saved product {}", saved.getName());
        return productMapper.toProductDTO(saved);
    }


    @Transactional
    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Brand brand = brandRepository.findById(productDTO.getBrand().id()).orElseThrow(BrandNotFoundException::new);
        Category category = categoryRepository.findById(productDTO.getCategory().getId()).orElseThrow(CategoryNotFoundException::new);
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        Product updated = productMapper.updateProductDTO(productDTO, product);
        updated.setBrand(brand);
        updated.setCategory(category);
        Product saved = productRepository.save(updated);
        log.info("Product with ID {} successfully updated", id);
        return productMapper.toProductDTO(saved);
    }

    @Transactional
    @Override
    public boolean deleteProduct(Long id) {
        Optional<Product> founded = productRepository.findById(id);
        if (founded.isPresent()) {
            productRepository.delete(founded.get());
            log.info("Product with ID {} successfully deleted", id);
            return true;
        }
        return false;
    }

    @Override
    public ProductAvailabilityDTO getProductAvailability(Long id) {
        log.debug("Getting product availability by id {}", id);
        return productRepository.findAvailabilityById(id).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean reserveProducts(List<OrderItemDTO> reservationRequestList) {
        if (reservationRequestList.isEmpty()) {
            log.warn("Reservation requests list is empty");
            return false;
        }
        Map<Long, Product> productsMap = productRepository.findAllById(
                reservationRequestList.stream()
                        .map(OrderItemDTO::productId)
                        .toList()
        ).stream().collect(Collectors.toMap(Product::getId, product -> product));

        List<Product> productsToReserve = new ArrayList<>();

        for (OrderItemDTO orderItem: reservationRequestList) {
            Product product = Optional.ofNullable(productsMap.get(orderItem.productId()))
                    .orElseThrow(() -> {
                        log.error("Product with ID {} not found", orderItem.productId());
                        return new ProductNotFoundException();
                    });
            if (product.getQuantity() < orderItem.quantity()) {
                log.warn("Product {} (ID: {}) is out of stock. Requested: {}, Available: {}",
                        product.getName(), product.getId(), orderItem.quantity(), product.getQuantity());
                return false;
            }
            product.setQuantity(product.getQuantity() - orderItem.quantity());
            productsToReserve.add(product);
        }
        productRepository.saveAll(productsToReserve);
        log.info("Successfully reserved {} products", productsToReserve.size());
        return true;
    }

    @Override
    @Transactional
    public ProductDTO uploadImage(Long id, String urlToImage) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(urlToImage);
        productImage.setProduct(product);
        product.addImage(productImage);
        productImageRepository.save(productImage);
        Product saved = productRepository.save(product);
        log.info("Image for {} uploaded successfully", product.getName());
        return productMapper.toProductDTO(saved);
    }
}
