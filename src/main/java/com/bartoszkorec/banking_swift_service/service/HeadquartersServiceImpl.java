package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadquartersServiceImpl implements HeadquartersService {

    private final HeadquartersRepository headquartersRepository;
    private final LocationService locationService;
    private final BankMapper bankMapper;

    @Autowired
    public HeadquartersServiceImpl(HeadquartersRepository headquartersRepository, LocationService locationService, BankMapper bankMapper) {
        this.headquartersRepository = headquartersRepository;
        this.locationService = locationService;
        this.bankMapper = bankMapper;
    }

    @Override
    public Headquarters processHeadquarters(Headquarters headquarters) {

        Location location = locationService.processLocation(headquarters.getLocation());
        headquarters.setLocation(location);
        return headquartersRepository.findById(headquarters.getSwiftCode())
                .orElseGet(() -> headquartersRepository.save(headquarters));
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {
        Headquarters headquarters = headquartersRepository.findById(swiftCode)
                .orElseThrow(() -> new NoResultException("cannot find headquarters with swift code: " + swiftCode));
        return bankMapper.toDTO(headquarters);
    }
}
