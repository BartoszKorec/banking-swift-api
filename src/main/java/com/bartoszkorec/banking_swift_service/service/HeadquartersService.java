package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;

public interface HeadquartersService {

    void addHeadquartersToDatabase(BankDTO headquartersDTO);

    BankDTO findBySwiftCode(String swiftCode);

    void deleteHeadquartersFromDatabase(String swiftCode);
}
