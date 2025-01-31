package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Country;

public interface CountryService {

    void processCountry(Country country);
}
