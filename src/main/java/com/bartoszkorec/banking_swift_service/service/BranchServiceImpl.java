package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.exception.BankExistsInDatabaseException;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.CorrespondingHeadquartersNotFoundException;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final LocationService locationService;
    private final HeadquartersRepository headquartersRepository;
    private final BankMapper bankMapper;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, LocationService locationService, HeadquartersRepository headquartersRepository, BankMapper bankMapper) {
        this.branchRepository = branchRepository;
        this.locationService = locationService;
        this.headquartersRepository = headquartersRepository;
        this.bankMapper = bankMapper;
    }

    @Override
    public void addBranchDTOToDatabase(BankDTO branchDTO) {

        String swiftCode = branchDTO.getSwiftCode();
        if (branchRepository.existsById(swiftCode)) {
            throw new BankExistsInDatabaseException("Cannot add bank to database with swift code: " + swiftCode + ". Bank already exists.");
        }
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
}
