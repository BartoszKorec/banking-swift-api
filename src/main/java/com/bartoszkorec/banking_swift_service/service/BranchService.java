package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.parser.BranchDTO;

public interface BranchService {
    void processBranch(BranchDTO branchDTO);
}
