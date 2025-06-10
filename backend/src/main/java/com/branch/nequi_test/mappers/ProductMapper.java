package com.branch.nequi_test.mappers;

import com.branch.nequi_test.dto.req.ProductDto;
import com.branch.nequi_test.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "nombre", source = "name")
    @Mapping(target = "existencia", source = "stock")
    ProductDto productToDto(Product product);

    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "stock", source = "existencia")
    @Mapping(target = "id", ignore = true)
    Product dtoToProduct(ProductDto dto);
}
