package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.parser.HeadquarterDTO;

public interface HeadquarterService {

    void processHeadquarter(HeadquarterDTO headquarterDTO);
}
