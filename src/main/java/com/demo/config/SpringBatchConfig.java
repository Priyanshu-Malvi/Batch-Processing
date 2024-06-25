package com.demo.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.demo.dto.EmployeeDtoClass;
import com.demo.model.EmployeeModel;
import com.demo.repository.EmployeeRepository;

@Configuration
public class SpringBatchConfig {
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private PlatformTransactionManager platformTransactionManager;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JobCompletionListener jobCompletionListener;
	
	@Autowired
	private StepCompletionListener stepCompletionListener;
	
	@Autowired
	private EmployeeProcessor employeeProcessor;
	
	@Autowired
	private ChunkDuplicateProcessor chunkDuplicateProcessor;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBatchConfig.class);
	
	@Bean
    @StepScope
    public ItemStreamReader<EmployeeDtoClass> reader(
            @Value("#{jobParameters['inputFilePath']}") String filePath) {
		LOGGER.info("Inside Reader");
        return new DelegatingItemStreamReader(filePath);
    }	
	
	@Bean
	public CompositeItemProcessor<EmployeeDtoClass, EmployeeModel> compositeProcessor(){
		List<ItemProcessor<?, ?>> delegates = new ArrayList<>();
		delegates.add(chunkDuplicateProcessor);
		delegates.add(employeeProcessor);		
		CompositeItemProcessor<EmployeeDtoClass, EmployeeModel> processor = new CompositeItemProcessor<>();
		processor.setDelegates(delegates);
		LOGGER.info("Inside Processor");
		return processor;
	}
	
	// WRITER
	@Bean
	public EmployeeWriter employeeWritrer(final EmployeeRepository employeeRepository) {
		LOGGER.info("Inside Writer");
		return new EmployeeWriter(employeeRepository);
	}
	
	// STEP
	@Bean
	public Step step(JobRepository repository, PlatformTransactionManager manager,
			EmployeeRepository employeeRepository) {
		LOGGER.info("Configuring Step");
		return new StepBuilder("step1", repository)
				.<EmployeeDtoClass, EmployeeModel>chunk(500, manager)
				.reader(reader(null))
				.processor(compositeProcessor())
				.writer(employeeWritrer(employeeRepository))
				.faultTolerant().skip(EmployeeWriteException.class)
				.listener(stepCompletionListener)
				.build();
	}

	@Bean
	public Step deleteTempFileStep(JobRepository repository, PlatformTransactionManager manager) {
		return new StepBuilder("deleteTempFileStep", repository).tasklet((contribution, chunkContext) -> {
			String filePath = chunkContext.getStepContext().getJobParameters().get("inputFilePath").toString();
			if (filePath != null) {
				File file = new File(filePath);
				if (file.exists()) {
					if (file.delete()) {
						LOGGER.info("Deleted temporary file: {}", filePath);
					} else {
						LOGGER.error("Failed to delete temporary file: {}", filePath);
					}
				}
			}
			return RepeatStatus.FINISHED;
		}, manager).build();
	}
	
	// JOB
	@Bean
	public Job employeeJob(JobRepository repository, 
							PlatformTransactionManager manager,
							EmployeeRepository employeeRepository) {		
		LOGGER.info("Configuring Job");
		return new JobBuilder("job", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(jobCompletionListener)
				.start(step(repository, manager, employeeRepository))
				.next(deleteTempFileStep(repository, manager))
				.build();
	}
	
}
