package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.exception.*;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessor;
import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final LocationService locationService;
    private final HeadquartersRepository headquartersRepository;
    private final BankMapper bankMapper;
    private final SwiftDataProcessor processor;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, LocationService locationService, HeadquartersRepository headquartersRepository, BankMapper bankMapper, SwiftDataProcessor processor) {
        this.branchRepository = branchRepository;
        this.locationService = locationService;
        this.headquartersRepository = headquartersRepository;
        this.bankMapper = bankMapper;
        this.processor = processor;
    }

    @Override
    public void addBranchDTOToDatabase(BankDTO branchDTO) {

        branchDTO = validateBranchDTO(branchDTO);
        Branch branch = bankMapper.toBranchEntity(branchDTO);
        Location location = locationService.findOrCreateLocation(branch.getLocation());
        branch.setLocation(location);
        String hqSwiftCode = branch.getHeadquarters().getSwiftCode();
        Headquarters headquarters = headquartersRepository.findById(hqSwiftCode)
                .orElseThrow(() -> new CorrespondingHeadquartersNotFoundException("No corresponding headquarters found for the branch with SWIFT code: " + branch.getSwiftCode()));
        branch.setHeadquarters(headquarters);
        branchRepository.save(branch);
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

    @Override
    public BankDTO validateBranchDTO(BankDTO branchDTO) {

        branchDTO = processor.processAndValidateBankDTO(branchDTO);

        branchDTO.setHeadquarters(false);
        String swiftCode = branchDTO.getSwiftCode();
        if (swiftCode.endsWith("XXX")) {
            throw new InvalidBranchException("Invalid branch SWIFT code. A SWIFT code ending with 'XXX' indicates a headquarters: " + swiftCode);
        }
        if (branchRepository.existsById(swiftCode)) {
            throw new BankExistsInDatabaseException("Cannot add bank to database with swift code: " + swiftCode + ". Bank already exists.");
        }
        return branchDTO;
    }
}
