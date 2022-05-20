package com.example.logmanager.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);

    public void validate(String... args) {
        LOGGER.info("Validating the user input ... ");      
        
        if (args.length < 1) {
            throw new IllegalArgumentException("Error: You need to specify the filepath ");
        }
        
        validateFilePath(args[0]);
        
    }

    private void validateFilePath(String logFilePath) {
        LOGGER.info("Log file name : {}", logFilePath);

        try {
            File file = new ClassPathResource("files/" + logFilePath).getFile();
            if (!file.exists()) {
                file = new ClassPathResource(logFilePath).getFile();
                if (!file.exists()) {
                    file = new File(logFilePath);
                }
            }

            if (!file.exists()) {
                throw new FileNotFoundException("ERROR::::Unable to open the file " + logFilePath);
            }
        } catch (IOException e) {
            LOGGER.error("Error::: file cannot be found");
        }
    }

   
}
