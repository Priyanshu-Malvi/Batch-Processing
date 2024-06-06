package com.demo.dto;

public class EmployeeDtoClass {
	
	private String employeeId;
	private String fullname;
	private Integer number;
	private String company;
	private boolean isDuplicate;
	
	public EmployeeDtoClass() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EmployeeDtoClass(String employeeId, String fullname, Integer number, String company, boolean isDuplicate) {
		super();
		this.employeeId = employeeId;
		this.fullname = fullname;
		this.number = number;
		this.company = company;
		this.isDuplicate = isDuplicate;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}
	
	
}
