package com.bartoszkorec.banking_swift_service.mapper.parser;

import com.bartoszkorec.banking_swift_service.dto.parser.CountryDTO;
import com.bartoszkorec.banking_swift_service.entity.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface CountryMapper {

    @Mapping(source = "iso2Code", target = "iso2Code")
    @Mapping(source = "countryName", target = "countryName")
    CountryDTO toDto(Country country);

    @Mapping(source = "iso2Code", target = "iso2Code")
    @Mapping(source = "countryName", target = "countryName")
    Country toEntity(CountryDTO countryDTO);
}
