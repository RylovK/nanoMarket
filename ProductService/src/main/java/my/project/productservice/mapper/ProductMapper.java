package my.project.productservice.mapper;

import my.project.productservice.dto.ProductDTO;
import my.project.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, BrandMapper.class})
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductDTO productDTO);

    @Mapping(target = "id", ignore = true)
    Product updateProductDTO(ProductDTO productDTO, @MappingTarget Product product);
}
