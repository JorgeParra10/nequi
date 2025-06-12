package com.accenture.infraestructura.manage_http.controller;

import com.accenture.dominio.interfaces.IBranchService;
import com.accenture.infraestructura.manage_http.dto.response.BranchResponseDto;
import com.accenture.infraestructura.manage_http.dto.response.BranchWithFranchiseDto;
import com.accenture.infraestructura.manage_http.dto.response.PageResponseDto;
import com.accenture.infraestructura.manage_http.mapper.BranchResponseDtoMapper;
import com.accenture.infraestructura.manage_http.mapper.BranchWithFranchiseDtoMapper;
import com.accenture.infraestructura.manage_http.mapper.PageResponseDtoMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static com.accenture.infraestructura.util.ConstantsInfraestructure.DEFAULT_PAGE;
import static com.accenture.infraestructura.util.ConstantsInfraestructure.DEFAULT_SIZE;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Branch Controller", description = "Endpoints for managing branches")
@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {
    private final IBranchService branchService;
    private final PageResponseDtoMapper pageResponseDtoMapper;
    private final BranchWithFranchiseDtoMapper branchWithFranchiseDtoMapper;
    private final BranchResponseDtoMapper branchResponseDtoMapper;

    @Operation(summary = "Create a new branch", description = "Creates a new branch associated with a given franchise.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Branch created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BranchResponseDto> createBranch(
            @Parameter(description = "Name of the branch", required = true) @RequestParam(name = "name") String name,
            @Parameter(description = "ID of the franchise", required = true) @RequestParam(name = "franchiseId") Long franchiseId) {
        return branchService.createBranch(name, franchiseId)
            .map(branchResponseDtoMapper::toDto);
    }

    @Operation(summary = "Get all branches with their franchises", description = "Returns a paged list of all branches with their associated franchises.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Branches retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/with-franchise")
    public Mono<PageResponseDto<BranchWithFranchiseDto>> getAllBranchesWithFranchise(
            @Parameter(description = "Page number", required = true) @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
            @Parameter(description = "Page size", required = true) @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) int size) {
        return branchService.getAllBranchesWithFranchisePaged(page, size)
            .map(pageResponse -> pageResponseDtoMapper.toDto(pageResponse, branchWithFranchiseDtoMapper::toDto));
    }

    @Operation(summary = "Update branch name", description = "Updates the name of an existing branch by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Branch updated successfully"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @PutMapping("/{id}")
    public Mono<BranchResponseDto> updateBranchName(
            @Parameter(description = "ID of the branch to update", required = true) @PathVariable(name = "id") Long id,
            @Parameter(description = "New name for the branch", required = true) @RequestParam(name = "name") String name) {
        return branchService.updateBranchName(id, name)
            .map(branchResponseDtoMapper::toDto);
    }
}