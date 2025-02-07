package com.bartoszkorec.banking_swift_service;

import org.springframework.boot.SpringApplication;

public class TestBankingSwiftServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(BankingSwiftServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
