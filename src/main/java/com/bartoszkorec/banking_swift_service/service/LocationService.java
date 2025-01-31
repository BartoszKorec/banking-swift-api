package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.parser.LocationDTO;

public interface LocationService {

    void processLocation(LocationDTO locationDTO);
}
