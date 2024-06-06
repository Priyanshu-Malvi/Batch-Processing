package com.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.EmployeeModel;




@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeModel, Long>{
	
	public List<EmployeeModel> findByFullname(String fullname);
	
}
