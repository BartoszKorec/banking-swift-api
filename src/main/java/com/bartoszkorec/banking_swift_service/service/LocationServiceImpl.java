package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final CountryService countryService;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, CountryService countryService) {
        this.locationRepository = locationRepository;
        this.countryService = countryService;
    }

    @Override
    public Location processLocation(Location location) {

        location.setCountry(countryService.processCountry(location.getCountry()));
        return locationRepository.findByAddressLine(location.getAddressLine())
                .orElseGet(() -> locationRepository.save(location));
    }
}
