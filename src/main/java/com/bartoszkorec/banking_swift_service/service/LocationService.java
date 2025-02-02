package com.bartoszkorec.banking_swift_service.service;


import com.bartoszkorec.banking_swift_service.entity.Location;

public interface LocationService {

    Location processLocation(Location location);
}
