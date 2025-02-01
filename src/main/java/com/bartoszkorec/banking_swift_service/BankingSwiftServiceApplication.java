package com.bartoszkorec.banking_swift_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingSwiftServiceApplication {

//    @Value("${swift.file.path}")
//    private String SwiftFilePath;
//	private final SwiftFileProcessorService swiftFileProcessorService;
//
//	@Autowired
//	public BankingSwiftServiceApplication(SwiftFileProcessorService swiftFileProcessorService) {
//		this.swiftFileProcessorService = swiftFileProcessorService;
//	}

    public static void main(String[] args) {
        SpringApplication.run(BankingSwiftServiceApplication.class, args);
    }

//	@Bean
//	public CommandLineRunner commandLineRunner() {
//		return runner -> {
//			swiftFileProcessorService.processSwiftFile();
//		};
//	}
}
