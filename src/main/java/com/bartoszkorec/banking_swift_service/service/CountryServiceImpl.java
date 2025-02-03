package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.mapper.CountryMapper;
import com.bartoszkorec.banking_swift_service.repository.CountryRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    @Override
    public Country processCountry(Country country) {

        return countryRepository.findById(country.getIso2Code())
                .orElseGet(() -> countryRepository.save(country));
    }

    @Override
    public CountryDTO findByIso2Code(String countryISO2code) {
        Country country = countryRepository.findById(countryISO2code)
                .orElseThrow(() -> new NoResultException("cannot find country with ISO2 code: " + countryISO2code));
        return countryMapper.toDTO(country);
    }
}
