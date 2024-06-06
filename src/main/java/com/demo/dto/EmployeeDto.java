package com.demo.dto;

public record EmployeeDto(
//		 Long id,
		 String employeeId,
		 String fullname,
		 Integer number,
		 String company) {

	public EmployeeDto {
        // Constructor logic here
    }

	public String employeeId() {
		return employeeId;
	}

	public String fullname() {
		return fullname;
	}

	public Integer number() {
		return number;
	}

	public String company() {
		return company;
	}
	 
	
}
