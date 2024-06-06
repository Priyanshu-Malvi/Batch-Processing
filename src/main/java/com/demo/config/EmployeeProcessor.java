package com.demo.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.dto.EmployeeDto;
import com.demo.dto.EmployeeDtoClass;
import com.demo.model.EmployeeModel;
import com.demo.repository.EmployeeRepository;

@Component
public class EmployeeProcessor implements ItemProcessor<EmployeeModel, EmployeeModel> {
	
	 @Autowired
	 private EmployeeRepository employeeRepository;

	@Override
	public EmployeeModel process(EmployeeModel item) throws Exception {		

		List<EmployeeModel> existingEmployeeOptional = employeeRepository.findByFullname(item.getFullname());
		
        if (!existingEmployeeOptional.isEmpty()) {
        	System.out.println("Duplicate found in database: "+ existingEmployeeOptional.get(0).getFullname());       	
        	return null;
        }       
        
		return item;
	}

}

