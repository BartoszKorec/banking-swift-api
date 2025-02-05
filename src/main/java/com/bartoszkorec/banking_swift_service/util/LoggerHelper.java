package com.bartoszkorec.banking_swift_service.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LoggerHelper {

    public static void logInfo(String infoMessage) {
        log.info(infoMessage);
    }

    public static void logWarning(String warningMessage) {
        log.warn(warningMessage);
    }

    public static void logWarning(String warningMessage, int lineNumber) {
        if (lineNumber > 1) {
            log.warn("Line {}: {}", lineNumber, warningMessage);
        }
    }

    public static void logError(String errorMessage) {
        log.error(errorMessage);
    }
}
