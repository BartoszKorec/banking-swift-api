package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
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

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, LocationService locationService, HeadquartersRepository headquartersRepository) {
        this.branchRepository = branchRepository;
        this.locationService = locationService;
        this.headquartersRepository = headquartersRepository;}

    @Override
    public Branch processBranch(Branch branch) {

        Location location = locationService.processLocation(branch.getLocation());
        branch.setLocation(location);
        String hqSwiftCode = branch.getHeadquarters().getSwiftCode();
        Headquarters headquarters = headquartersRepository.findById(hqSwiftCode)
                .orElseThrow(() -> new NoResultException("Headquarters not found for SWIFT code: " + hqSwiftCode));
        branch.setHeadquarters(headquarters);
        return branchRepository.findById(branch.getSwiftCode())
                .orElseGet(() -> branchRepository.save(branch));
    }
}
