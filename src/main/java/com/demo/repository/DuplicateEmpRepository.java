package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.DuplicateEmployeeModel;
import com.demo.model.JobModel;
@Repository
public interface DuplicateEmpRepository extends JpaRepository<DuplicateEmployeeModel, Long>{
	 List<DuplicateEmployeeModel> findByJob(JobModel job);
}
