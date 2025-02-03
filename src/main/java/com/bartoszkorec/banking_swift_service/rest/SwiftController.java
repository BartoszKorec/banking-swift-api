package com.bartoszkorec.banking_swift_service.rest;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.service.SwiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwiftController {

    private final SwiftService service;

    @Autowired
    public SwiftController(SwiftService service) {
        this.service = service;
    }

    @GetMapping("/swift-codes/{swiftCode}")
    BankDTO getSwiftCode(@PathVariable String swiftCode) {
        return service.findBySwiftCode(swiftCode);
    }
}
