package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.exception.BankExistsInDatabaseException;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.InvalidBranchException;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessor;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HeadquartersServiceImpl implements HeadquartersService {

    private final HeadquartersRepository headquartersRepository;
    private final LocationService locationService;
    private final BankMapper bankMapper;
    private final BranchService branchService;
    private final SwiftDataProcessor processor;

    @Autowired
    public HeadquartersServiceImpl(HeadquartersRepository headquartersRepository, LocationService locationService, BankMapper bankMapper, BranchService branchService, SwiftDataProcessor processor) {
        this.headquartersRepository = headquartersRepository;
        this.locationService = locationService;
        this.bankMapper = bankMapper;
        this.branchService = branchService;
        this.processor = processor;
    }

    @Override
    public void addHeadquartersToDatabase(BankDTO headquartersDTO) {

        headquartersDTO = processor.processAndValidateHeadquartersDTO(headquartersDTO);
        Set<BankDTO> branches = headquartersDTO.getBranches();
        try {
            branches.forEach(processor::processAndValidateBranchDTO);
        } catch (InvalidBranchException e) {
            throw new InvalidBranchException("Invalid branch in headquarters with SWIFT code: " + headquartersDTO.getSwiftCode() + ". " + e.getMessage());
        }
        String swiftCode = headquartersDTO.getSwiftCode();
        if (headquartersRepository.existsById(swiftCode)) {
            throw new BankExistsInDatabaseException("Cannot add bank to database with swift code: " + swiftCode + ". Bank already exists.");
        }

        Headquarters headquarters = bankMapper.toHeadquartersEntity(headquartersDTO);
        Location location = locationService.findOrCreateLocation(headquarters.getLocation());
        headquarters.setLocation(location);

        // Set branches to null initially to avoid potential issues with saving the headquarters entity.
        // Branches will be added separately to ensure the headquarters entity is fully persisted before associating branches.
        headquarters.setBranches(null);
        headquartersRepository.save(headquarters);

        branches.forEach(branch -> {
            try {
                branchService.addBranchDTOToDatabase(branch);
            } catch (BankExistsInDatabaseException ignore) {
            } // conflicts will be ignored
        });
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
