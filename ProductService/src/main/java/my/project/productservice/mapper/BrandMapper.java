package my.project.productservice.mapper;

import my.project.productservice.dto.BrandDTO;
import my.project.productservice.persistence.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {


    BrandDTO toDto(Brand brand);

    @Mapping(target = "id", ignore = true)
    Brand toEntity(BrandDTO brandDTO);


    @Mapping(target = "id", ignore = true)
    Brand updateBrand(BrandDTO brandDTO, @MappingTarget Brand brand);
}
