package com.example.logmanager.model;

public class LogFileLocation {
    private static LogFileLocation INSTANCE;

    private String logFilePath;

    private LogFileLocation() {
    }

    public static LogFileLocation getInstance() {
        if (INSTANCE == null) INSTANCE = new LogFileLocation();
        return INSTANCE;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

}
