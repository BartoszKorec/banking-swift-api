package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;

public interface BankService {

    BankDTO findBySwiftCode(String swiftCode);
    CountryDTO findByCountryISO2code(String countryISO2code);
    void addBankToDatabase(BankDTO bank);
    void deleteBank(String swiftCode);
}
