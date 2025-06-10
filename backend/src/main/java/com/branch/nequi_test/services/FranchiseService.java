package com.branch.nequi_test.services;

import com.branch.nequi_test.dto.req.BranchDto;
import com.branch.nequi_test.dto.req.FranchiseDto;
import com.branch.nequi_test.mappers.BranchMapper;
import com.branch.nequi_test.mappers.FranchiseMapper;
import com.branch.nequi_test.models.Franchise;
import com.branch.nequi_test.repositories.BranchRepository;
import com.branch.nequi_test.repositories.FranchiseRepository;
import com.branch.nequi_test.utils.ApiResponseObj;
import com.branch.nequi_test.utils.exeptions.ResourceNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FranchiseService {
    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final FranchiseMapper franchiseMapper;
    private final BranchMapper branchMapper;

    public FranchiseService(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.franchiseMapper = Mappers.getMapper(FranchiseMapper.class);
        this.branchMapper=Mappers.getMapper(BranchMapper.class);
    }

    public ApiResponseObj<FranchiseDto> saveFranchise(FranchiseDto franchise) {
        var res= franchiseMapper.franchiseToDto(franchiseRepository.save(franchiseMapper.dtoToFranchise(franchise)));
        return new ApiResponseObj<>(res);

    }

    public ApiResponseObj<FranchiseDto> updateFranchise(Long franchiseId, FranchiseDto franchiseUpdate) {

        Franchise __ = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found"));
        var objSave = franchiseMapper.dtoToFranchise(franchiseUpdate);
        objSave.setId(franchiseId);

        return new ApiResponseObj<>(franchiseMapper.franchiseToDto( franchiseRepository.save(objSave)));

    }

    public ApiResponseObj<BranchDto> addBranch(Long franchiseId, BranchDto branch) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new RuntimeException("Franchise not found"));
        var branchToSave = branchMapper.dtoToBranch(branch);
        branchToSave.setFranchise(franchise);
        return  new ApiResponseObj<>(branchMapper.branchToDto(branchRepository.save(branchToSave)));
    }

    public ApiResponseObj<List<FranchiseDto>> getAllFranchises() {
        var results = franchiseRepository.findAll();
        var mappedData =results.stream().map(franchiseMapper::franchiseToDto).toList();

        return new ApiResponseObj<>(mappedData);
    }
}
