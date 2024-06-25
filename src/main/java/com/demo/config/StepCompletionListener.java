package com.demo.config;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.demo.model.JobModel;
import com.demo.model.StepMaster;
import com.demo.repository.JobInfoRepository;
import com.demo.repository.StepMasterRepo;

@Component
public class StepCompletionListener implements StepExecutionListener {
	
	@Autowired
	private StepMasterRepo stepRepo;
	
	@Autowired
	private JobInfoRepository jobRepo;
	
	private Set<String> processedEmployees;
	
	private Long jobId;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
        // Code to execute before the step starts
        System.out.println("Step " + stepExecution.getStepName() + " is starting.");
        
        processedEmployees = new HashSet<>();
        
        JobModel currentJob = jobRepo.findById(stepExecution.getJobExecutionId()).get();
        
        jobId = stepExecution.getJobExecutionId();
        
        StepMaster step = new StepMaster();
        step.setStepId(stepExecution.getId());
        step.setJobExecutionId(stepExecution.getJobExecutionId());
        step.setStepName(stepExecution.getStepName());
        step.setStepCreateTime(stepExecution.getCreateTime());
        step.setStepStartTime(stepExecution.getStartTime());
        step.setStepLastUpdatedTime(stepExecution.getLastUpdated());
        step.setStepStatus(stepExecution.getStatus().toString());
        step.setJob(currentJob);
        
        stepRepo.save(step);
    }
	
	@Override
    public ExitStatus afterStep(StepExecution stepExecution) {
		
		StepMaster existingStep = stepRepo.findById(stepExecution.getId()).get();
		existingStep.setStepLastUpdatedTime(stepExecution.getLastUpdated());
		existingStep.setStepStatus(stepExecution.getStatus().toString());
		existingStep.setException("NULL");
		
		if(stepExecution.getStatus().toString().equals("FAILED")) {
			
			StringBuilder errorMessage = new StringBuilder();
			for (Throwable t : stepExecution.getFailureExceptions()) {
		        // Option 1: Store only the error message
		        errorMessage.append(t.getMessage()).append(", ");

		        // Option 2: Store only the class name
		        // errorMessage.append(t.getClass().getSimpleName()).append("\n");
		    }
            existingStep.setException(errorMessage.toString()); 
			
			JobModel job = jobRepo.findById(stepExecution.getJobExecutionId()).get();
			job.setJobBatchStatus(stepExecution.getStepName() + "IS FAILED");
			job.setJobExitStatus("STEP_FAILED");
			job.setJobEndTime(LocalDateTime.now());
			
			jobRepo.save(job);
		}
		
		stepRepo.save(existingStep);

		processedEmployees.clear();
		
        // Code to execute after the step completes
        System.out.println("Step " + stepExecution.getStepName() + " has completed.");
        return stepExecution.getExitStatus();
    }
	
	public Set<String> getProcessedEmployees() {
        return processedEmployees;
    }
	
	public Long getJobId() {
		return jobId;
	}
	
}
