package com.branch.nequi_test.controllers;

import com.branch.nequi_test.configs.RouterConstants;
import com.branch.nequi_test.dto.req.ProductDto;
import com.branch.nequi_test.services.BranchService;
import com.branch.nequi_test.utils.ApiResponseObj;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RouterConstants.BRANCH)
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    /**
     * Agrega un producto a una sucursal específica.
     *
     * @param id       ID de la sucursal donde se agregará el producto.
     * @param product  Objeto con la información del producto a agregar.
     * @return ResponseEntity con el producto agregado.
     */
    @Operation(summary = "Agregar un producto a una sucursal", description = "Permite agregar un nuevo producto a una sucursal específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado correctamente"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para el producto")
    })
    @PostMapping(RouterConstants.BRANCH_ADD_PRODUCT)
    public ApiResponseObj<ProductDto> addProduct(@PathVariable Long id, @RequestBody ProductDto product) {
        return branchService.addProduct(id, product);
    }
}
