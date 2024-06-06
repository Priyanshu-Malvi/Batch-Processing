package com.demo.config;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.demo.dto.EmployeeDto;
import com.demo.dto.EmployeeDtoClass;



public class EmployeeMapper implements FieldSetMapper<EmployeeDtoClass> {

	  private final String[] columnNames;

	    public EmployeeMapper(String[] columnNames) {
	        this.columnNames = columnNames;
	    }

	    @Override
	    public EmployeeDtoClass mapFieldSet(FieldSet fieldSet) throws BindException {
	        String employeeId = null;
	        String fullname = null;
	        Integer number = null;
	        String company = null;

	        // Assign values based on the available columns
	        for (String columnName : columnNames) {
	            switch (columnName) {
	                case "employeeId":
	                    employeeId = fieldSet.readString("employeeId");
	                    break;
	                case "fullname":
	                    fullname = fieldSet.readString("fullname");
	                    break;
	                case "number":
	                    number = fieldSet.readInt("number", 0); // Default to 0 if the field is not present
	                    break;
	                case "company":
	                    company = fieldSet.readString("company");
	                    break;
	                default:
	                    break;
	            }
	        }

	        return new EmployeeDtoClass(employeeId, fullname, number, company, false);
	    }
}
