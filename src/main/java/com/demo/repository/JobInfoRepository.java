package com.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.JobModel;

@Repository
public interface JobInfoRepository extends JpaRepository<JobModel, Long>{

}
