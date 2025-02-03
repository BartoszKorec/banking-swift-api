package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadquartersServiceImpl implements HeadquartersService {

    private final HeadquartersRepository headquartersRepository;
    private final LocationService locationService;

    @Autowired
    public HeadquartersServiceImpl(HeadquartersRepository headquartersRepository, LocationService locationService) {
        this.headquartersRepository = headquartersRepository;
        this.locationService = locationService;
    }

    @Override
    public Headquarters processHeadquarters(Headquarters headquarters) {

        Location location = locationService.processLocation(headquarters.getLocation());
        headquarters.setLocation(location);
        return headquartersRepository.findById(headquarters.getSwiftCode())
                .orElseGet(() -> headquartersRepository.save(headquarters));
    }
}
