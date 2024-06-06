package com.demo.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TerminateConfig {
	
	@Bean
	public SimpleJobOperator terminateJobOperator(JobExplorer jobExplorer,
	                                         JobRepository jobRepository,
	                                         JobRegistry jobRegistry,
	                                         JobLauncher jobLauncher) {

	    SimpleJobOperator jobOperator = new SimpleJobOperator();
	    jobOperator.setJobExplorer(jobExplorer);
	    jobOperator.setJobRepository(jobRepository);
	    jobOperator.setJobRegistry(jobRegistry);
	    jobOperator.setJobLauncher(jobLauncher);

	    return jobOperator;
	}
	 	
}
