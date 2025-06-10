package com.branch.nequi_test.mappers;

import com.branch.nequi_test.dto.req.FranchiseDto;
import com.branch.nequi_test.models.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FranchiseMapper {

    FranchiseMapper INSTANCE = Mappers.getMapper(FranchiseMapper.class);

    @Mapping(target = "nombre", source = "name")
    FranchiseDto franchiseToDto(Franchise franchise);

    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "id", ignore = true)
    Franchise dtoToFranchise(FranchiseDto dto);

}
