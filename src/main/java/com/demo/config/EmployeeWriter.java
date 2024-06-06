package com.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.dto.EmployeeDtoClass;
import com.demo.model.EmployeeModel;
import com.demo.repository.EmployeeRepository;

@Component
public class EmployeeWriter implements ItemWriter<EmployeeModel> {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeWriter.class);
	
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeWriter(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void write(Chunk<? extends EmployeeModel> chunk) throws Exception {
    	try {
    		logger.info("Saving the records");
            employeeRepository.saveAll(chunk);
    		
    	} catch (Exception e) {
    		logger.error("Error writing chunk", e);
    		throw e;
    	}
    }
    
}
