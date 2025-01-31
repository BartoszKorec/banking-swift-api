package com.bartoszkorec.banking_swift_service.mapper.parser;

import com.bartoszkorec.banking_swift_service.dto.parser.HeadquarterDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring", uses = {LocationMapper.class})
public interface HeadquarterMapper {

    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "location", target = "location")
    HeadquarterDTO toDTO(Headquarter headquarter);

    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "location", target = "location")
    @Mapping(target = "branches", expression = "java(new java.util.HashSet<>())")
    Headquarter toEntity(HeadquarterDTO headquarterDTO);
}
