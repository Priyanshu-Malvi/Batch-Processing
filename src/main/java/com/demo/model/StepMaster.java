package com.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_info")
public class StepMaster {
	
	@Id
	private Long stepId;
	private Long jobExecutionId;
	private String stepName;
	private LocalDateTime stepCreateTime;
	private LocalDateTime stepStartTime;
	private LocalDateTime stepLastUpdatedTime;
	private String stepStatus;
	private String exception;
	
	@ManyToOne
	@JoinColumn(name = "job_id", nullable = false)
	@JsonBackReference
	private JobModel job;
	
	public StepMaster() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StepMaster(Long stepId, Long jobExecutionId, String stepName, LocalDateTime stepCreateTime,
			LocalDateTime stepStartTime, LocalDateTime stepLastUpdatedTime, String stepStatus, String exception,
			JobModel job) {
		super();
		this.stepId = stepId;
		this.jobExecutionId = jobExecutionId;
		this.stepName = stepName;
		this.stepCreateTime = stepCreateTime;
		this.stepStartTime = stepStartTime;
		this.stepLastUpdatedTime = stepLastUpdatedTime;
		this.stepStatus = stepStatus;
		this.exception = exception;
		this.job = job;
	}

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

	public Long getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public LocalDateTime getStepCreateTime() {
		return stepCreateTime;
	}

	public void setStepCreateTime(LocalDateTime stepCreateTime) {
		this.stepCreateTime = stepCreateTime;
	}

	public LocalDateTime getStepStartTime() {
		return stepStartTime;
	}

	public void setStepStartTime(LocalDateTime stepStartTime) {
		this.stepStartTime = stepStartTime;
	}

	public LocalDateTime getStepLastUpdatedTime() {
		return stepLastUpdatedTime;
	}

	public void setStepLastUpdatedTime(LocalDateTime stepLastUpdatedTime) {
		this.stepLastUpdatedTime = stepLastUpdatedTime;
	}

	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public JobModel getJob() {
		return job;
	}

	public void setJob(JobModel job) {
		this.job = job;
	}

}
