package com.demo.config;

import java.util.Set;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.demo.dto.EmployeeDtoClass;
import com.demo.model.EmployeeModel;

@Component
public class ChunkDuplicateProcessor implements ItemProcessor<EmployeeDtoClass, EmployeeModel> {

	@Autowired
	private StepCompletionListener listener;

	@Override
	public EmployeeModel process(EmployeeDtoClass item) throws Exception {

		Set<String> processedEmployees = listener.getProcessedEmployees();
		
		if (processedEmployees.contains(item.getFullname())) {
			System.out.println("Duplicate found in current chunk: " + item.getFullname());
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
}
