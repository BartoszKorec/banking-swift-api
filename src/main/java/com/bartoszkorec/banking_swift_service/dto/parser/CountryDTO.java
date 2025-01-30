package com.bartoszkorec.banking_swift_service.dto.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {

    private String iso2code;
    private String countryName;
}
