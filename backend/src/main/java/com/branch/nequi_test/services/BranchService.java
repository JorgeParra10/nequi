package com.branch.nequi_test.services;

import com.branch.nequi_test.dto.req.ProductDto;
import com.branch.nequi_test.mappers.ProductMapper;
import com.branch.nequi_test.models.Branch;
import com.branch.nequi_test.repositories.BranchRepository;
import com.branch.nequi_test.repositories.ProductRepository;
import com.branch.nequi_test.utils.ApiResponseObj;
import com.branch.nequi_test.utils.exeptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
public class BranchService {
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    public BranchService(BranchRepository branchRepository, ProductRepository productRepository) {
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.productMapper= Mappers.getMapper(ProductMapper.class);
    }

    @Transactional
    public ApiResponseObj<ProductDto> addProduct(Long branchId, ProductDto product) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found."));

        var productToSave = productMapper.dtoToProduct(product);
        productToSave.setBranch(branch);

        return new ApiResponseObj<>(productMapper.productToDto( productRepository.save(productToSave)));
    }
}
