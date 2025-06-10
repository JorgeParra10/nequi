package com.branch.nequi_test.controllers;

import com.branch.nequi_test.configs.RouterConstants;
import com.branch.nequi_test.dto.req.BranchDto;
import com.branch.nequi_test.dto.req.FranchiseDto;
import com.branch.nequi_test.services.FranchiseService;
import com.branch.nequi_test.utils.ApiResponseObj;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RouterConstants.FRANCHISE)
public class FranchiseController {

    private final FranchiseService franchiseService;

    public FranchiseController(FranchiseService franchiseService) {
        this.franchiseService = franchiseService;
    }

    /**
     * Crea una nueva franquicia.
     *
     * @param franchise Objeto con la información de la franquicia.
     * @return ApiResponseObj con la franquicia creada.
     */
    @Operation(summary = "Crear una nueva franquicia", description = "Permite registrar una nueva franquicia en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Franquicia creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para la franquicia")
    })
    @PostMapping
    public ApiResponseObj<FranchiseDto> saveFranchise(@RequestBody FranchiseDto franchise) {
        return franchiseService.saveFranchise(franchise);
    }

    /**
     * Actualizar una franquicia.
     *
     * @param franchise Objeto con la información de la franquicia.
     * @return ApiResponseObj con la franquicia actualizada.
     */
    @Operation(summary = "Actualizar datos de una franquicia", description = "Permite actualizar una franquicia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Franquicia actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para la franquicia")
    })
    @PatchMapping(RouterConstants.COMMON_PATH_OPERATION)
    public ApiResponseObj<FranchiseDto> updateFranchise(@PathVariable Long id, @RequestBody FranchiseDto franchise) {
        return franchiseService.updateFranchise(id,franchise);
    }

    /**
     * Agrega una nueva sucursal a una franquicia específica.
     *
     * @param id     ID de la franquicia a la que se agregará la sucursal.
     * @param branch Objeto con la información de la sucursal a agregar.
     * @return ApiResponseObj con la sucursal creada.
     */
    @Operation(summary = "Agregar una sucursal a una franquicia", description = "Permite agregar una nueva sucursal a una franquicia existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucursal agregada correctamente"),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para la sucursal")
    })
    @PostMapping(RouterConstants.FRANCHISE_ADD_BRANCHES)
    public ApiResponseObj<BranchDto> saveBranch(@PathVariable Long id, @RequestBody BranchDto branch) {
        return franchiseService.addBranch(id, branch);
    }

    /**
     * Obtiene todas las franquicias registradas en el sistema.
     *
     * @return ApiResponseObj con la lista de franquicias.
     */
    @Operation(summary = "Obtener todas las franquicias", description = "Devuelve una lista de todas las franquicias registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de franquicias obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping
    public ApiResponseObj<List<FranchiseDto>> getAllFranchises() {
        return franchiseService.getAllFranchises();
    }
}
