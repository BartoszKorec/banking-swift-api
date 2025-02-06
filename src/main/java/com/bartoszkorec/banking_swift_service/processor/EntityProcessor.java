package com.bartoszkorec.banking_swift_service.processor;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityProcessor {

    private final BankMapper bankMapper;
    private final CountryService countryService;

    @Autowired
    public EntityProcessor(BankMapper bankMapper, CountryService countryService) {
        this.bankMapper = bankMapper;
        this.countryService = countryService;
    }

    public Headquarters processHeadquarters(BankDTO bankDTO) {

        Headquarters headquarters = bankMapper.toHeadquartersEntity(bankDTO);
        Location location = headquarters.getLocation();
        location.setCountry(countryService.findOrCreateCountry(location.getCountry()));
        headquarters.setLocation(location);
        return headquarters;
    }

    public Branch processBranch(BankDTO bankDTO) {

        Branch branch = bankMapper.toBranchEntity(bankDTO);
        Location location = branch.getLocation();
        location.setCountry(countryService.findOrCreateCountry(location.getCountry()));
        branch.setLocation(location);
        return branch;
    }
}
