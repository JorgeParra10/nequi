package com.branch.nequi_test.mappers;

import com.branch.nequi_test.dto.req.BranchDto;
import com.branch.nequi_test.models.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BranchMapper {
    BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);

    @Mapping(target = "nombre", source = "name")
    BranchDto branchToDto(Branch franchise);

    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "id", ignore = true)
    Branch dtoToBranch(BranchDto dto);
}
