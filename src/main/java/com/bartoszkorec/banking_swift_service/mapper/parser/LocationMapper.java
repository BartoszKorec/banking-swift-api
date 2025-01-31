package com.bartoszkorec.banking_swift_service.mapper.parser;

import com.bartoszkorec.banking_swift_service.dto.parser.LocationDTO;
import com.bartoszkorec.banking_swift_service.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring", uses = {CountryMapper.class})
public interface LocationMapper {

    @Mapping(source = "addressLine", target = "addressLine")
    @Mapping(source = "country", target = "countryDTO")
    LocationDTO toDto(Location location);

    @Mapping(source = "addressLine", target = "addressLine")
    @Mapping(source = "countryDTO", target = "country")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branches", expression = "java(new java.util.HashSet<>())")
    @Mapping(target = "headquarters", expression = "java(new java.util.HashSet<>())")
    Location toEntity(LocationDTO locationDTO);
}
