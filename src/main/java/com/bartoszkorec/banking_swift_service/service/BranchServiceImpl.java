package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public void processBranch(Branch branch) {

        branchRepository.save(branch);
    }
}
