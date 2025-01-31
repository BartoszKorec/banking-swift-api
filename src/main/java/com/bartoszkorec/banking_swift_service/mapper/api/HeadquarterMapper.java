package com.bartoszkorec.banking_swift_service.mapper.api;

import com.bartoszkorec.banking_swift_service.dto.api.HeadquarterDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HeadquarterMapper {

    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(source = "name", target = "bankName")
    @Mapping(source = "location.addressLine", target = "address")
    @Mapping(source = "location.country.countryName", target = "countryName")
    @Mapping(source = "location.country.iso2Code", target = "countryISO2")
    @Mapping(target = "headquarter", expression = "java(true)")
    // I dunno why not isHeadquarter :/
    @Mapping(target = "branches", expression = "java(new java.util.HashSet<>())")
    HeadquarterDTO toDTO(Headquarter headquarter);

    @Mapping(source = "address", target = "location.addressLine")
    @Mapping(source = "bankName", target = "name")
    @Mapping(source = "countryISO2", target = "location.country.iso2Code")
    @Mapping(source = "countryName", target = "location.country.countryName")
    @Mapping(source = "swiftCode", target = "swiftCode")
    @Mapping(target = "branches", expression = "java(new java.util.HashSet<>())")
    Headquarter toEntity(HeadquarterDTO headquarterDTO);
}
