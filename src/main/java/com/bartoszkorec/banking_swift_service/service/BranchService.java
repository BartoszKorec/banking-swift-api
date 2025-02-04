package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;

public interface BranchService {
    void addBranchDTOToDatabase(BankDTO branchDTO);

    BankDTO findBySwiftCode(String swiftCode);

    void deleteBranchFromDatabase(String swiftCode);
}
