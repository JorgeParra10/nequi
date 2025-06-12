package com.accenture.infraestructura.manage_http.mapper;

import com.accenture.dominio.model.Branch;
import com.accenture.infraestructura.manage_http.dto.response.BranchResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchResponseDtoMapper {
    BranchResponseDto toDto(Branch branch);
}
