package com.accenture.infraestructura.manage_http.mapper;

import com.accenture.dominio.model.responses.BranchWithFranchise;
import com.accenture.infraestructura.manage_http.dto.response.BranchWithFranchiseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BranchWithFranchiseDtoMapper {
    BranchWithFranchiseDtoMapper INSTANCE = Mappers.getMapper(BranchWithFranchiseDtoMapper.class);
    @Mapping(target = "id", source = "branch.id")
    @Mapping(target = "name", source = "branch.name")
    BranchWithFranchiseDto toDto(BranchWithFranchise pair);
}
