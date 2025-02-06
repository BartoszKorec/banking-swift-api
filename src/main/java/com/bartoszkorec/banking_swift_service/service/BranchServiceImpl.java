package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.exception.BankExistsInDatabaseException;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.CorrespondingHeadquartersNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.InvalidBranchException;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.processor.EntityProcessor;
import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final HeadquartersRepository headquartersRepository;
    private final EntityProcessor entityProcessor;
    private final BankMapper bankMapper;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, HeadquartersRepository headquartersRepository, EntityProcessor entityProcessor, BankMapper bankMapper) {
        this.branchRepository = branchRepository;
        this.entityProcessor = entityProcessor;
        this.headquartersRepository = headquartersRepository;
        this.bankMapper = bankMapper;
    }

    @Override
    public void addBranchDTOToDatabase(BankDTO branchDTO) {

        String swiftCode = branchDTO.getSwiftCode();
        if (swiftCode.endsWith("XXX")) {
            throw new InvalidBranchException("Cannot add bank's branch with SWIFT code: " + swiftCode
                    + ". SWIFT code ending with 'XXX' indicates a headquarters");
        }
        if (branchRepository.existsById(swiftCode)) {
            throw new BankExistsInDatabaseException("Cannot add bank to database with swift code: " + swiftCode + ". Bank already exists.");
        }
        if (!headquartersRepository.existsById(swiftCode.substring(0, 8) + "XXX")) {
            throw new CorrespondingHeadquartersNotFoundException("No corresponding headquarters found for the branch with SWIFT code: " + swiftCode);
        }

        try {
            Branch branch = entityProcessor.processBranch(branchDTO);
            branchRepository.save(branch);
        } catch (PersistenceException e) {
            throw new InvalidBranchException("Database error encountered when adding branch with SWIFT code: " + swiftCode);
        }
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {
        Branch branch = branchRepository.findById(swiftCode)
                .orElseThrow(() -> new BankNotFoundException("Cannot find bank with swift code: " + swiftCode));
        return bankMapper.toDTO(branch);
    }

    @Override
    public void deleteBranchFromDatabase(String swiftCode) {
        if (!branchRepository.existsById(swiftCode)) {
            throw new BankNotFoundException("Cannot delete bank with swift code: " + swiftCode + ". Bank does not exists in database.");
        }
        branchRepository.deleteById(swiftCode);
    }
}
