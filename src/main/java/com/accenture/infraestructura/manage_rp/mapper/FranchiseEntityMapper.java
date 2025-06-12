package com.accenture.infraestructura.manage_rp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.accenture.dominio.model.Franchise;
import com.accenture.infraestructura.manage_rp.entity.FranchiseEntity;

@Mapper(componentModel = "spring")
public interface FranchiseEntityMapper {
    FranchiseEntityMapper INSTANCE = Mappers.getMapper(FranchiseEntityMapper.class);

    @Mapping(target = "id", ignore = true)
    FranchiseEntity toEntity(Franchise model);
    Franchise toModel(FranchiseEntity entity);
}
