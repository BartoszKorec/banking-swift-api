package com.bartoszkorec.banking_swift_service.rest;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.service.SwiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
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
    public ResponseEntity<String> addBank(@RequestBody BankDTO bank) {
        service.addBankToDatabase(bank);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bank with SWIFT code: " + bank.getSwiftCode() + " added successfully.");
    }

    @DeleteMapping("/swift-codes/{swiftCode}")
    public ResponseEntity<Void> deleteBank(@PathVariable String swiftCode) {
        service.deleteBank(swiftCode.toUpperCase());
        return ResponseEntity.noContent().build();
    }
}
