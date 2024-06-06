package com.demo.config;

public class EmployeeWriteException extends Exception{
	
	public EmployeeWriteException(String message) {
        super(message);
    }

    public EmployeeWriteException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
