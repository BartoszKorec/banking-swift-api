package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void processLocation(Location location) {

        locationRepository.save(location);
    }
}
