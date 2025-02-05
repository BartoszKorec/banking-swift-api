package com.bartoszkorec.banking_swift_service.reader;

import com.bartoszkorec.banking_swift_service.exception.FileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logError;

public abstract class TSVFileReader {

    public static List<LineRecord> readTSVFile(String filePath) {

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            String errorMessage = "File " + path.getFileName() + " does not exist!";
            logError(errorMessage);
            throw new FileException(errorMessage);
        }

        List<LineRecord> records;
        AtomicInteger lineCounter = new AtomicInteger(2);
        try (Stream<String> lines = Files.lines(path)) {
            records = lines
                    .skip(1)  // skip header
                    .map(line -> new LineRecord(line.split("\t"), lineCounter.getAndIncrement()))
                    .toList();
        } catch (IOException e) {
            String errorMessage = String.format(
                    "Failed to read file '%s'. Ensure the path is correct and the file is accessible. Error: %s",
                    path, e.getMessage()
            );
            logError(errorMessage);
            throw new FileException(errorMessage);
        }
        return records;
    }
}

