package com.bartoszkorec.banking_swift_service.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryDTO {

    private String countryISO2;
    private String countryName;
    private Set<BankDTO> swiftCodes;
}
