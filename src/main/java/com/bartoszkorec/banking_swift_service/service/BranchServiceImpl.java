package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.exception.*;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final LocationService locationService;
    private final BankMapper bankMapper;
    private final HeadquartersRepository headquartersRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, LocationService locationService, BankMapper bankMapper, HeadquartersRepository headquartersRepository) {
        this.branchRepository = branchRepository;
        this.locationService = locationService;
        this.bankMapper = bankMapper;
        this.headquartersRepository = headquartersRepository;
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
            Branch branch = bankMapper.toBranchEntity(branchDTO);
            Location location = locationService.findOrCreateLocation(branch.getLocation());
            branch.setLocation(location);
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
