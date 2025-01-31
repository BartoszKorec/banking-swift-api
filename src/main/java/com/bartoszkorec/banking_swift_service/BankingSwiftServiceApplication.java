package com.bartoszkorec.banking_swift_service;

import com.bartoszkorec.banking_swift_service.service.SwiftFileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingSwiftServiceApplication {

	private final SwiftFileProcessorService swiftFileProcessorService;

	@Autowired
	public BankingSwiftServiceApplication(SwiftFileProcessorService swiftFileProcessorService) {
		this.swiftFileProcessorService = swiftFileProcessorService;
	}

	public static void main(String[] args) {
		SpringApplication.run(BankingSwiftServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return runner -> {
			swiftFileProcessorService.processSwiftFile();
		};
	}
}
