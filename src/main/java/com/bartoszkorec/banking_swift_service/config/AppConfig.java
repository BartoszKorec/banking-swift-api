package com.bartoszkorec.banking_swift_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${swift.file.path}")
    private String filePath;
}
