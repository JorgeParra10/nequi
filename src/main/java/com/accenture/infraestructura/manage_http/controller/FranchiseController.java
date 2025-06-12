package com.accenture.infraestructura.manage_http.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.accenture.dominio.interfaces.IFranchiseService;
import com.accenture.infraestructura.manage_http.dto.response.FranchiseResponseDto;
import com.accenture.infraestructura.manage_http.dto.response.PageResponseDto;
import com.accenture.infraestructura.manage_http.mapper.FranchiseResponseDtoMapper;
import com.accenture.infraestructura.manage_http.mapper.PageResponseDtoMapper;
import com.accenture.infraestructura.util.ConstantsInfraestructure;

import reactor.core.publisher.Mono;

@Tag(name = "Franchise Controller", description = "Endpoints for managing franchises")
@RestController
@RequestMapping("/franchises")
@RequiredArgsConstructor
public class FranchiseController {
    private final IFranchiseService franchiseService;
    private final PageResponseDtoMapper pageResponseDtoMapper;
    private final FranchiseResponseDtoMapper franchiseResponseDtoMapper;

    @Operation(summary = "Create a new franchise", description = "Creates a new franchise with the specified name.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Franchise created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponseDto> createFranchise(
            @Parameter(description = "Name of the franchise", required = true) @Valid @RequestParam(name = "name") String name) {
        return franchiseService.createFranchise(name)
            .map(franchiseResponseDtoMapper::toDto);
    }

    @Operation(summary = "Update franchise name", description = "Updates the name of an existing franchise by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Franchise updated successfully"),
        @ApiResponse(responseCode = "404", description = "Franchise not found")
    })
    @PutMapping("/{id}")
    public Mono<FranchiseResponseDto> updateFranchiseName(
            @Parameter(description = "ID of the franchise to update", required = true) @PathVariable(name = "id") Long id,
            @Parameter(description = "New name for the franchise", required = true) @Valid @RequestParam(name = "name") String name) {
        return franchiseService.updateFranchiseName(id, name)
            .map(franchiseResponseDtoMapper::toDto);
    }

    @Operation(summary = "Get all franchises (paginated)", description = "Retrieves a paginated list of all franchises.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Franchises retrieved successfully")
    })
    @GetMapping
    public Mono<PageResponseDto<FranchiseResponseDto>> getAllFranchisesPaged(
            @Parameter(description = "Page number", required = false) @RequestParam(name = "page", defaultValue = ConstantsInfraestructure.DEFAULT_PAGE) int page,
            @Parameter(description = "Page size", required = false) @RequestParam(name = "size", defaultValue = ConstantsInfraestructure.DEFAULT_SIZE) int size) {
        return franchiseService.getAllFranchisesPaged(page, size)
            .map(pageResponse -> pageResponseDtoMapper.toDto(pageResponse, franchiseResponseDtoMapper::toDto));
    }
}
