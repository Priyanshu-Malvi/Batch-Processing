package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.StepMaster;

@Repository
public interface StepMasterRepo extends JpaRepository<StepMaster, Long>{
	
	public List<StepMaster> findByJobExecutionId(Long jobExecutionId);
	
}
