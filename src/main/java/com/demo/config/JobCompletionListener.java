package com.demo.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.model.JobModel;
import com.demo.repository.JobInfoRepository;


@Component
public class JobCompletionListener implements JobExecutionListener{
	
	@Autowired
	private JobInfoRepository jobRepo;
		
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Job started at: "+ jobExecution.getStartTime());
		System.out.println("Status of the Job: "+jobExecution.getStatus());	
		
		JobModel job = new JobModel();
		job.setJobId(jobExecution.getJobId());
		job.setJobCreateTime(jobExecution.getCreateTime());
		job.setJobStartTime(jobExecution.getStartTime());
		job.setJobEndTime(jobExecution.getEndTime());
		job.setJobLastUpdatedTime(jobExecution.getLastUpdated());
		job.setJobBatchStatus(jobExecution.getStatus().toString());
		job.setJobExitStatus(jobExecution.getExitStatus().toString());
//		job.setJobParams(jobExecution.getJobParameters().getString("inputFilePath"));
		String fullPath = jobExecution.getJobParameters().getString("inputFilePath");
	    Path path = Paths.get(fullPath);
	    String fileName = path.getFileName().toString();
	    job.setJobParams(fileName);
		jobRepo.save(job);
		
//		System.out.println("before id " + jobExecution.getJobId());
//		System.out.println("before create time " + jobExecution.getCreateTime());
//		System.out.println("before start time " + jobExecution.getStartTime());
//		System.out.println("before end time " + jobExecution.getEndTime());
//		System.out.println("before last update " + jobExecution.getLastUpdated());
//		System.out.println("before status " + jobExecution.getStatus());
//		System.out.println("before exit status " + jobExecution.getExitStatus());
//		System.out.println("before job parameter " + jobExecution.getJobParameters().toString());
		
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		
		JobModel existingJob = jobRepo.findById(jobExecution.getJobId()).get();
		existingJob.setJobEndTime(jobExecution.getEndTime());
		existingJob.setJobLastUpdatedTime(jobExecution.getLastUpdated());
		existingJob.setJobBatchStatus(jobExecution.getStatus().toString());
		existingJob.setJobExitStatus(jobExecution.getExitStatus().toString());
		
		jobRepo.save(existingJob);
		
		if(jobExecution.getStatus()==BatchStatus.COMPLETED) {
			System.out.println("Job Ended at: "+ jobExecution.getEndTime());
		       System.out.println("Status of the Job: "+jobExecution.getStatus());
		}
		
	}
	
}
