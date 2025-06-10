package com.branch.nequi_test.controllers;

import com.branch.nequi_test.configs.RouterConstants;
import com.branch.nequi_test.dto.ProductStockDto;
import com.branch.nequi_test.dto.req.ProductDto;
import com.branch.nequi_test.dto.req.StockProductDto;
import com.branch.nequi_test.services.ProductService;
import com.branch.nequi_test.utils.ApiResponseObj;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RouterConstants.PRODUCT)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id ID del producto a eliminar.
     * @return ApiResponseObj sin contenido.
     */
    @Operation(summary = "Eliminar un producto", description = "Permite eliminar un producto mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "ID de producto inválido")
    })
    @DeleteMapping(RouterConstants.COMMON_PATH_OPERATION)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    /**
     * Modifica el stock de un producto.
     *
     * @param id    ID del producto a actualizar.
     * @param stock nueva cantidad del producto.
     * @return ApiResponseObj con el producto actualizado.
     */
    @Operation(summary = "Actualizar stock de un producto", description = "Permite modificar la cantidad de stock de un producto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Valor de stock inválido")
    })
    @PatchMapping(RouterConstants.PRODUCT_STOCK)
    public ApiResponseObj<ProductDto> updatedStock(@PathVariable Long id, @RequestBody StockProductDto stock) {
        return productService.updateStock(id, stock);
    }

    /**
     * Obtiene el producto con mayor stock en las sucursales por franquicia.
     *
     * @param id ID de la franquicia donde se buscarán los productos con mayor stock.
     * @return ApiResponseObj con la lista de productos de mayor stock en cada sucursal.
     */
    @Operation(summary = "Obtener el producto con mayor stock", description = "Devuelve el producto con mayor stock en una sucursal específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto con mayor stock encontrado"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada o sin productos"),
            @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping(RouterConstants.PRODUCT_HIGHER_STOCK)
    public ApiResponseObj<List<ProductStockDto>> productHigherStock(@PathVariable Long id) {
        return productService.getProductsWhenStockHigher(id);
    }
}
