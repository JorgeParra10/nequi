package com.branch.nequi_test.services;

import com.branch.nequi_test.dto.ProductStockDto;
import com.branch.nequi_test.dto.req.ProductDto;
import com.branch.nequi_test.dto.req.StockProductDto;
import com.branch.nequi_test.mappers.ProductMapper;
import com.branch.nequi_test.models.Product;
import com.branch.nequi_test.repositories.ProductRepository;
import com.branch.nequi_test.utils.ApiResponseObj;
import com.branch.nequi_test.utils.exeptions.ResourceNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.productMapper = Mappers.getMapper(ProductMapper.class);

    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public ApiResponseObj<ProductDto> updateStock(Long id, StockProductDto newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStock(newStock.getExistencia());
        return new ApiResponseObj<>( productMapper.productToDto(productRepository.save(product)));
    }

    public ApiResponseObj<List<ProductStockDto>> getProductsWhenStockHigher(Long franchiseId) {
        return new ApiResponseObj<>( productRepository.findHighestStockProductsByFranchise(franchiseId));
    }
}
