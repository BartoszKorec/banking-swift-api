package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.exception.*;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import com.bartoszkorec.banking_swift_service.processor.EntityProcessor;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HeadquartersServiceImpl implements HeadquartersService {

    private final HeadquartersRepository headquartersRepository;
    private final BankDTOProcessor bankDTOProcessor;
    private final EntityProcessor entityProcessor;
    private final BankMapper bankMapper;

    @Autowired
    public HeadquartersServiceImpl(HeadquartersRepository headquartersRepository, BankMapper bankMapper, BankDTOProcessor bankDTOProcessor, EntityProcessor entityProcessor) {
        this.headquartersRepository = headquartersRepository;
        this.bankMapper = bankMapper;
        this.bankDTOProcessor = bankDTOProcessor;
        this.entityProcessor = entityProcessor;
    }

    @Override
    public void addHeadquartersToDatabase(BankDTO headquartersDTO) {

        if (!headquartersDTO.isHeadquarters()) {
            throw new InvalidHeadquartersException("Cannot add headquarters with SWIFT code: " + headquartersDTO.getSwiftCode()
                    + ". SWIFT code not ending with 'XXX' indicates a branch");
        }

        String swiftCode = headquartersDTO.getSwiftCode();
        if (headquartersRepository.existsById(swiftCode)) {
            throw new BankExistsInDatabaseException("Cannot add bank to database with swift code: " + swiftCode + ". Bank already exists.");
        }

        Headquarters headquarters = entityProcessor.processHeadquarters(headquartersDTO);

        Set<Branch> validatedBranches = Collections.emptySet();
        try {
            if (headquartersDTO.getBranches() != null) {
                validatedBranches = headquartersDTO.getBranches().stream()
                        .map(bankDTOProcessor::processBankDTO)
                        .filter(b -> !b.isHeadquarters()) // Filter branches that are incorrectly marked as headquarters
                        .map(entityProcessor::processBranch)
                        .collect(Collectors.toSet());
            }
        } catch (InvalidFieldsException e) {
            throw new InvalidBranchException("Invalid branch in headquarters with SWIFT code: " + headquartersDTO.getSwiftCode() + ". " + e.getMessage());
        }

        try {
            validatedBranches.forEach(b -> b.setHeadquarters(headquarters));

            headquarters.setBranches(validatedBranches);

            headquartersRepository.save(headquarters);
        } catch (RuntimeException e) {
            throw new InvalidHeadquartersException("Database error encountered when adding headquarters with SWIFT code: "
                    + swiftCode + ". Error: " + e.getClass().getSimpleName() + "Message: " + e.getMessage());
        }
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {
        Headquarters headquarters = headquartersRepository.findById(swiftCode)
                .orElseThrow(() -> new BankNotFoundException("Cannot find bank with swift code: " + swiftCode));
        return bankMapper.toDTO(headquarters);
    }

    @Override
    public void deleteHeadquartersFromDatabase(String swiftCode) {
        if (!headquartersRepository.existsById(swiftCode)) {
            throw new BankNotFoundException("Cannot delete bank with swift code: " + swiftCode + ". Bank does not exists in database.");
        }
        headquartersRepository.deleteById(swiftCode);
    }

}
