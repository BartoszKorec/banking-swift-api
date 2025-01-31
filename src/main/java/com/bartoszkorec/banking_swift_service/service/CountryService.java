package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.parser.CountryDTO;

public interface CountryService {

    void processCountry(CountryDTO countryDTO);
}
