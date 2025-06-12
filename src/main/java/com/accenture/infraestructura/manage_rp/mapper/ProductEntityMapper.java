package com.accenture.infraestructura.manage_rp.mapper;

import com.accenture.dominio.model.Product;
import com.accenture.infraestructura.manage_rp.entity.ProductEntity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    ProductEntityMapper INSTANCE = Mappers.getMapper(ProductEntityMapper.class);

    Product toModel(ProductEntity entity);
    ProductEntity toEntity(Product model);
}
