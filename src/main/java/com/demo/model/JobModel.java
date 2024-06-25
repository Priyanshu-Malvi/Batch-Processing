package com.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_master")
public class JobModel {
	
	@Id
	private Long jobId;
	private LocalDateTime jobCreateTime;
	private LocalDateTime jobStartTime;
	private LocalDateTime jobEndTime;
	private LocalDateTime jobLastUpdatedTime;
	private String jobParams;
	private String jobBatchStatus;
	private String jobExitStatus;
	
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
	@JsonManagedReference
    private List<StepMaster> stepList;
	
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<DuplicateEmployeeModel> duplicateEmployees;

	public JobModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JobModel(Long jobId, LocalDateTime jobCreateTime, LocalDateTime jobStartTime, LocalDateTime jobEndTime,
			LocalDateTime jobLastUpdatedTime, String jobParams, String jobBatchStatus, String jobExitStatus,
			List<StepMaster> stepList) {
		super();
		this.jobId = jobId;
		this.jobCreateTime = jobCreateTime;
		this.jobStartTime = jobStartTime;
		this.jobEndTime = jobEndTime;
		this.jobLastUpdatedTime = jobLastUpdatedTime;
		this.jobParams = jobParams;
		this.jobBatchStatus = jobBatchStatus;
		this.jobExitStatus = jobExitStatus;
		this.stepList = stepList;
	}

	public List<DuplicateEmployeeModel> getDuplicateEmployees() {
		return duplicateEmployees;
	}

	public void setDuplicateEmployees(List<DuplicateEmployeeModel> duplicateEmployees) {
		this.duplicateEmployees = duplicateEmployees;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public LocalDateTime getJobCreateTime() {
		return jobCreateTime;
	}

	public void setJobCreateTime(LocalDateTime jobCreateTime) {
		this.jobCreateTime = jobCreateTime;
	}

	public LocalDateTime getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(LocalDateTime jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public LocalDateTime getJobEndTime() {
		return jobEndTime;
	}

	public void setJobEndTime(LocalDateTime jobEndTime) {
		this.jobEndTime = jobEndTime;
	}

	public LocalDateTime getJobLastUpdatedTime() {
		return jobLastUpdatedTime;
	}

	public void setJobLastUpdatedTime(LocalDateTime jobLastUpdatedTime) {
		this.jobLastUpdatedTime = jobLastUpdatedTime;
	}

	public String getJobParams() {
		return jobParams;
	}

	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}

	public String getJobBatchStatus() {
		return jobBatchStatus;
	}

	public void setJobBatchStatus(String jobBatchStatus) {
		this.jobBatchStatus = jobBatchStatus;
	}

	public String getJobExitStatus() {
		return jobExitStatus;
	}

	public void setJobExitStatus(String jobExitStatus) {
		this.jobExitStatus = jobExitStatus;
	}

	public List<StepMaster> getStepList() {
		return stepList;
	}

	public void setStepList(List<StepMaster> stepList) {
		this.stepList = stepList;
	}
		
}
