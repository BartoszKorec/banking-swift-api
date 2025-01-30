package com.bartoszkorec.banking_swift_service.dto.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    private String addressLine;
    private CountryDTO countryDTO;
}
