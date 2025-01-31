package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void processCountry(Country country) {

        countryRepository.save(country);
    }
}
