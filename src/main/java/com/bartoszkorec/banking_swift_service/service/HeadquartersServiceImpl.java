package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.exception.*;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HeadquartersServiceImpl implements HeadquartersService {

    private final HeadquartersRepository headquartersRepository;
    private final LocationService locationService;
    private final BankMapper bankMapper;
    private final BankDTOProcessor bankDTOProcessor;
    private final BranchService branchService;

    @Autowired
    public HeadquartersServiceImpl(HeadquartersRepository headquartersRepository, LocationService locationService, BankMapper bankMapper, BankDTOProcessor bankDTOProcessor, BranchService branchService) {
        this.headquartersRepository = headquartersRepository;
        this.locationService = locationService;
        this.bankMapper = bankMapper;
        this.bankDTOProcessor = bankDTOProcessor;
        this.branchService = branchService;
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

        Set<BankDTO> validatedDTOBranches = Collections.emptySet();
        try {
            if (headquartersDTO.getBranches() != null) {
                validatedDTOBranches = headquartersDTO.getBranches().stream()
                        .map(bankDTOProcessor::processBankDTO)
                        .filter(b -> !b.isHeadquarters()) // Filter branches that are incorrectly marked as headquarters
                        .collect(Collectors.toSet());
            }
        } catch (InvalidFieldsException e) {
            throw new InvalidBranchException("Invalid branch in headquarters with SWIFT code: " + headquartersDTO.getSwiftCode() + ". " + e.getMessage());
        }

        try {
            headquartersDTO.setBranches(validatedDTOBranches);
            Headquarters headquarters = bankMapper.toHeadquartersEntity(headquartersDTO);

            Location location = locationService.findOrCreateLocation(headquarters.getLocation());
            headquarters.setLocation(location);

            headquarters.setBranches(Collections.emptySet());  // Temporarily set branches to an empty set to avoid circular dependency issues during the initial save
            headquartersRepository.saveAndFlush(headquarters);
        } catch (RuntimeException e) {
            throw new InvalidHeadquartersException("Database error encountered when adding headquarters with SWIFT code: " + swiftCode);
        }

        try {
            if (!validatedDTOBranches.isEmpty()) {
                validatedDTOBranches.forEach(branchService::addBranchDTOToDatabase);
            }
        } catch (RuntimeException e) {
            headquartersRepository.deleteById(headquartersDTO.getSwiftCode());
            throw new InvalidBranchException("Error occurred while saving inner headquarters' branches. Aborting adding headquarters with swift code: " + swiftCode);
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
