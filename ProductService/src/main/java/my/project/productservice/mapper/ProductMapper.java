package my.project.productservice.mapper;

import jakarta.validation.constraints.NotNull;
import my.project.productservice.dto.ProductAvailabilityDTO;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.entity.Product;
import my.project.productservice.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, BrandMapper.class})
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductDTO productDTO);

    @Mapping(target = "id", ignore = true)
    Product updateProductDTO(ProductDTO productDTO, @MappingTarget Product product);

    ProductAvailabilityDTO toProductAvailabilityDTO(Product product);

    default List<String> toImages(List<ProductImage> productImages) {
        return productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }

    default List<ProductImage> toProductImages(List<String> urls) {
        if (urls == null || urls.isEmpty()) return Collections.emptyList();
        return urls.stream()
                .map(url -> {
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(url);
                    return productImage;
                })
                .collect(Collectors.toList());
    }
}
