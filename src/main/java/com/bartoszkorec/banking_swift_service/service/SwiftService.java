package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;

public interface SwiftService {

    BankDTO findBySwiftCode(String swiftCode);
}
