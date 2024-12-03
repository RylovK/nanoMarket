package my.project.productservice.mapper;

import my.project.productservice.dto.BrandDTO;
import my.project.productservice.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandDTO toDto(Brand brand);
    Brand toEntity(BrandDTO brandDTO);

    Brand updateBrand(BrandDTO brandDTO, @MappingTarget Brand brand);
}
