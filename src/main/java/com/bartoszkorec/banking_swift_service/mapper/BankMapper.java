package com.bartoszkorec.banking_swift_service.mapper;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BankMapper {

    @Mapping(source = "name", target = "bankName")
    @Mapping(source = "location.addressLine", target = "address")
    @Mapping(source = "location.country.countryName", target = "countryName")
    @Mapping(source = "location.country.iso2Code", target = "countryISO2")
    @Mapping(target = "isHeadquarters", expression = "java(false)")
    @Mapping(target = "branches", expression = "java(null)")
    BankDTO toDTO(Branch branch);

    @Mapping(source = "name", target = "bankName")
    @Mapping(source = "location.addressLine", target = "address")
    @Mapping(source = "location.country.countryName", target = "countryName")
    @Mapping(source = "location.country.iso2Code", target = "countryISO2")
    @Mapping(target = "isHeadquarters", expression = "java(true)")
    BankDTO toDTO(Headquarters headquarters);

    @Mapping(source = "address", target = "location.addressLine")
    @Mapping(source = "bankName", target = "name")
    @Mapping(source = "countryISO2", target = "location.country.iso2Code")
    @Mapping(source = "countryName", target = "location.country.countryName")
    @Mapping(source = "swiftCode", qualifiedByName = "mapSwiftCode", target = "headquarters.swiftCode")
    Branch toBranchEntity(BankDTO bankDTO);

    @Mapping(source = "address", target = "location.addressLine")
    @Mapping(source = "bankName", target = "name")
    @Mapping(source = "countryISO2", target = "location.country.iso2Code")
    @Mapping(source = "countryName", target = "location.country.countryName")
    Headquarters toHeadquartersEntity(BankDTO bankDTO);

    default Object toEntity(BankDTO bankDTO) {
        if (bankDTO.isHeadquarters()) {
            return toHeadquartersEntity(bankDTO);
        } else {
            return toBranchEntity(bankDTO);
        }
    }

    @Named("mapSwiftCode")
    default String makeHeadquartersSwiftCode(String branchSwiftCode) {
        return branchSwiftCode.substring(0, 8) + "XXX";
    }
}
