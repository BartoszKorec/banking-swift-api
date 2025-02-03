package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwiftServiceImpl implements SwiftService {

    private final HeadquartersService headquartersService;
    private final BranchService branchService;
    private final BankMapper bankMapper;

    @Autowired
    public SwiftServiceImpl(HeadquartersService headquartersService, BranchService branchService, BankMapper bankMapper) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.bankMapper = bankMapper;
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {

        BankDTO bankDTO = null;
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
}
