package com.bartoszkorec.banking_swift_service.service;

import java.nio.file.Path;

public interface SwiftFileService {

    void processSwiftFile(Path path);
}
