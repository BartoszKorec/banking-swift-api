package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;

public interface BranchService {
    Branch processBranch(Branch branch);

    BankDTO findBySwiftCode(String swiftCode);

    BankDTO addBranch(BankDTO bank);

    void deleteBranch(String swiftCode);
}
