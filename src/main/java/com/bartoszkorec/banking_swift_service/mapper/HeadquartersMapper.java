package com.bartoszkorec.banking_swift_service.mapper;

import com.bartoszkorec.banking_swift_service.dto.HeadquartersDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HeadquartersMapper {

    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(source = "name", target = "bankName")
    @Mapping(source = "location.addressLine", target = "address")
    @Mapping(source = "location.country.countryName", target = "countryName")
    @Mapping(source = "location.country.iso2Code", target = "countryISO2")
    @Mapping(target = "headquarters", expression = "java(true)")
    // I dunno why not isHeadquarters :/
    @Mapping(target = "branches", expression = "java(new java.util.HashSet<>())")
    HeadquartersDTO toDTO(Headquarters headquarters);

    @Mapping(source = "address", target = "location.addressLine")
    @Mapping(source = "bankName", target = "name")
    @Mapping(source = "countryISO2", target = "location.country.iso2Code")
    @Mapping(source = "countryName", target = "location.country.countryName")
    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(target = "branches", expression = "java(new java.util.HashSet<>())")
    Headquarters toEntity(HeadquartersDTO headquartersDTO);
}
