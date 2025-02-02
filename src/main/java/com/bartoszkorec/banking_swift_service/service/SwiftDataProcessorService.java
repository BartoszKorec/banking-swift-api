package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;

import java.nio.file.Path;

public interface SwiftDataProcessorService {

    void processSwiftFile(Path path);
    void processDTO(BankDTO bankDTO);
}
