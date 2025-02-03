package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;

public interface HeadquartersService {

    Headquarters processHeadquarters(Headquarters headquarters);

    BankDTO findBySwiftCode(String swiftCode);

    BankDTO addHeadquarters(BankDTO bank);

    void deleteHeadquarters(String swiftCode);
}
