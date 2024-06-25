package com.demo.config;

import java.util.Set;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.dto.EmployeeDtoClass;
import com.demo.model.DuplicateEmployeeModel;
import com.demo.model.EmployeeModel;
import com.demo.model.JobModel;
import com.demo.repository.DuplicateEmpRepository;
import com.demo.repository.JobInfoRepository;

import jakarta.servlet.http.HttpSession;

@Component
public class ChunkDuplicateProcessor implements ItemProcessor<EmployeeDtoClass, EmployeeModel> {

	@Autowired
	private StepCompletionListener listener;
	
//	@Autowired
//	private DuplicateEmployeeService service;
	
	@Autowired
	private DuplicateEmpRepository duplicateEmpRepo;
	
	@Autowired
	private JobInfoRepository jobRepo;
	
	@Autowired
	private StepCompletionListener stepListner;
	
	@Override
	public EmployeeModel process(EmployeeDtoClass item) throws Exception {

		Set<String> processedEmployees = listener.getProcessedEmployees();

		 if (processedEmployees.contains(item.getFullname())) {
		        System.out.println("Duplicate found in current chunk: " + item.getFullname());
		        DuplicateEmployeeModel duplicateEmployee = mapDuplicateEmployee(item);
		        
		        duplicateEmpRepo.save(duplicateEmployee);
		        return null;
		    }

		processedEmployees.add(item.getFullname());

		return mapEmployee(item);
	}

	private EmployeeModel mapEmployee(EmployeeDtoClass item) {

		EmployeeModel employee = new EmployeeModel();
		employee.setEmployeeId(item.getEmployeeId());
		employee.setFullname(item.getFullname());
		employee.setNumber(item.getNumber());
		employee.setCompany(item.getCompany());

		return employee;
	}
	
	private DuplicateEmployeeModel mapDuplicateEmployee(EmployeeDtoClass item) {

		DuplicateEmployeeModel employee = new DuplicateEmployeeModel();
		employee.setEmployeeId(item.getEmployeeId());
		employee.setFullname(item.getFullname());
		employee.setNumber(item.getNumber());
		employee.setCompany(item.getCompany());
		
		JobModel currJob = jobRepo.findById(stepListner.getJobId()).get();
		
		employee.setJob(currJob);
		
		return employee;
	}
}
