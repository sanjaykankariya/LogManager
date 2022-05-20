package com.example.logmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.logmanager.service.LogManagerService;


@SpringBootApplication
public class LogManagerApplication implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogManagerApplication.class);
	 
    @Autowired
    private LogManagerService service;

	public static void main(String[] args) {
		SpringApplication.run(LogManagerApplication.class, args);		
	}

	@Override
	public void run(String... args) throws Exception {
		long startTime = System.currentTimeMillis();
		LOGGER.info("Execution of the program started !!");
		service.execute(args);
		long endTime = System.currentTimeMillis();
		LOGGER.info("Total time taken " + (endTime - startTime) + " milliseconds");
		LOGGER.info("Execution of the program ended !! ");			
	}
}


