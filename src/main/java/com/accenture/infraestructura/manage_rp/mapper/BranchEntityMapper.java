package com.accenture.infraestructura.manage_rp.mapper;

import com.accenture.dominio.model.Branch;
import com.accenture.infraestructura.manage_rp.entity.BranchEntity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BranchEntityMapper {
    BranchEntityMapper INSTANCE = Mappers.getMapper(BranchEntityMapper.class);

    Branch toModel(BranchEntity entity);
    BranchEntity toEntity(Branch model);
}
