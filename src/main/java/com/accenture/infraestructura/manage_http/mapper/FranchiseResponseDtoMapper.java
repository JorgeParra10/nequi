package com.accenture.infraestructura.manage_http.mapper;

import org.mapstruct.Mapper;

import com.accenture.dominio.model.Franchise;
import com.accenture.infraestructura.manage_http.dto.response.FranchiseResponseDto;

@Mapper(componentModel = "spring")
public interface FranchiseResponseDtoMapper {
    FranchiseResponseDto toDto(Franchise franchise);
}
