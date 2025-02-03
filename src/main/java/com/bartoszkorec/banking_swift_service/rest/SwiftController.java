package com.bartoszkorec.banking_swift_service.rest;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.service.SwiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SwiftController {

    private final SwiftService service;

    @Autowired
    public SwiftController(SwiftService service) {
        this.service = service;
    }

    @GetMapping("/swift-codes/{swiftCode}")
    public BankDTO getSwiftCode(@PathVariable String swiftCode) {
        return service.findBySwiftCode(swiftCode.toUpperCase());
    }

    @GetMapping("/swift-codes/country/{countryISO2code}")
    public CountryDTO getBanks(@PathVariable String countryISO2code) {
        return service.findByCountryISO2code(countryISO2code.toUpperCase());
    }

    @PostMapping("/swift-codes")
    public String addBank(@RequestBody BankDTO bank) {

        return service.addBank(bank);
    }

    @DeleteMapping("/swift-codes/{swiftCode}")
    public String deleteBank(@PathVariable String swiftCode) {

        return service.deleteBank(swiftCode.toUpperCase());
    }
}
