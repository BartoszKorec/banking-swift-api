package com.bartoszkorec.banking_swift_service;

import com.bartoszkorec.banking_swift_service.service.SwiftDataProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@SpringBootApplication
public class BankingSwiftServiceApplication {

//    @Value("${swift.file.path}")
//    private String swiftFilePath;
//	private final SwiftDataProcessorService swiftDataProcessorService;
//
//    @Autowired
//    public BankingSwiftServiceApplication(SwiftDataProcessorService swiftDataProcessorService) {
//        this.swiftDataProcessorService = swiftDataProcessorService;
//    }

    public static void main(String[] args) {
        SpringApplication.run(BankingSwiftServiceApplication.class, args);
    }

//	@Bean
//	public CommandLineRunner commandLineRunner() {
//		return runner -> {
//            swiftDataProcessorService.processSwiftFile(Path.of(swiftFilePath));
//		};
//	}
}
