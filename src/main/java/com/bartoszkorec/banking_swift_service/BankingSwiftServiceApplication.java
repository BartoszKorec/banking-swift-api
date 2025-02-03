package com.bartoszkorec.banking_swift_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
