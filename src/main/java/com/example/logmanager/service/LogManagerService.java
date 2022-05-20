package com.example.logmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.logmanager.model.LogFileLocation;
import com.example.logmanager.persist.PersistLogEvents;
import com.example.logmanager.validator.Validator;


@Service
public class LogManagerService {

    @Autowired
    private Validator validator;
    
    @Autowired
    private PersistLogEvents persistLogEvents;
    
    public void execute(String... args) {
        validator.validate(args);     
        LogFileLocation ctx = LogFileLocation.getInstance();
        ctx.setLogFilePath(args[0]);
        persistLogEvents.parseAndPersistEvents(ctx);
    }    
}
