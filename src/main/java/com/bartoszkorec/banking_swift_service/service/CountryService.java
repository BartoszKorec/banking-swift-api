package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.entity.Country;

public interface CountryService {

    Country findOrCreateCountry(Country country);

    CountryDTO findByIso2Code(String countryISO2code);
}
