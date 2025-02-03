package com.bartoszkorec.banking_swift_service.mapper;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    @Mapping(source = "iso2Code", target = "countryISO2")
    @Mapping(source = "locations", qualifiedByName = "mapBanks", target = "swiftCodes")
    CountryDTO toDTO(Country country);

    @Named("mapBanks")
    default Set<BankDTO> mapBanks(Set<Location> locations) {

        BankMapper bankMapper = Mappers.getMapper(BankMapper.class);
        return locations.stream()
                .flatMap(location -> {
                    Set<BankDTO> banks = new HashSet<>();
                    banks.addAll(location.getBranches().stream()
                            .map(bankMapper::toDTO)
                            .collect(Collectors.toSet()));
                    banks.addAll(location.getHeadquarters().stream()
                            .map(bankMapper::toDTO)
                            .collect(Collectors.toSet()));
                    return banks.stream();
                })
                .collect(Collectors.toSet());
    }
}