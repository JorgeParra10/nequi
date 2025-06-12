package com.accenture.infraestructura.manage_http.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.accenture.dominio.model.PageResponse;
import com.accenture.infraestructura.manage_http.dto.response.PageResponseDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = {BranchWithFranchiseDtoMapper.class, ProductWithBranchDtoMapper.class})
public interface PageResponseDtoMapper {
    PageResponseDtoMapper INSTANCE = Mappers.getMapper(PageResponseDtoMapper.class);

    default <D> PageResponseDto<D> toDto(PageResponse<D> pageResponse) {
        if (pageResponse == null) return null;
        return new PageResponseDto<>(
            pageResponse.getContent(),
            pageResponse.getPage(),
            pageResponse.getSize(),
            pageResponse.getTotalElements(),
            pageResponse.getTotalPages()
        );
    }

    default <T, D> PageResponseDto<D> toDto(PageResponse<T> pageResponse, Function<T, D> mapper) {
        if (pageResponse == null) return null;
        List<D> mappedContent = pageResponse.getContent() == null ? null : pageResponse.getContent().stream().map(mapper).collect(Collectors.toList());
        return new PageResponseDto<>(
            mappedContent,
            pageResponse.getPage(),
            pageResponse.getSize(),
            pageResponse.getTotalElements(),
            pageResponse.getTotalPages()
        );
    }
}
