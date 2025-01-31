package com.bartoszkorec.banking_swift_service.mapper.parser;

import com.bartoszkorec.banking_swift_service.dto.parser.BranchDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring", uses = {LocationMapper.class, HeadquarterMapper.class})
public interface BranchMapper {

    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "headquarter", target = "headquarter")
    @Mapping(source = "location", target = "location")
    BranchDTO toDTO(Branch branch);

    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "headquarter", target = "headquarter")
    @Mapping(source = "location", target = "location")
    Branch toEntity(BranchDTO branchDTO);
}
