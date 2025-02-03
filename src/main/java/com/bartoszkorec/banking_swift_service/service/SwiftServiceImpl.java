package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwiftServiceImpl implements SwiftService {

    private final HeadquartersService headquartersService;
    private final BranchService branchService;
    private final CountryService countryService;

    @Autowired
    public SwiftServiceImpl(HeadquartersService headquartersService, BranchService branchService, BankMapper bankMapper, CountryService countryService) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.countryService = countryService;
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {

        BankDTO bankDTO;
        try {
            bankDTO = headquartersService.findBySwiftCode(swiftCode);
        } catch (NoResultException e) {
            // Headquarters not found, try branches
            try {
                bankDTO = branchService.findBySwiftCode(swiftCode);
            } catch (NoResultException ex) {
                // Branch not found, return null
                return null;
            }
        }
        return bankDTO;
    }

    @Override
    public CountryDTO findByCountryISO2code(String countryISO2code) {
        CountryDTO countryDTO;
        try {
            countryDTO  = countryService.findByIso2Code(countryISO2code);
        } catch (NoResultException ex) {
            return null;
        }
        return countryDTO;
    }
}
