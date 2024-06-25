package com.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.demo.config.StepCompletionListener;
import com.demo.model.DuplicateEmployeeModel;
import com.demo.model.EmployeeModel;
import com.demo.model.JobModel;
import com.demo.model.StepMaster;
import com.demo.repository.DuplicateEmpRepository;
import com.demo.repository.EmployeeRepository;
import com.demo.repository.JobInfoRepository;
import com.demo.repository.StepMasterRepo;
import com.demo.service.ExcelExportService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@EnableAsync
@RestController
@RequiredArgsConstructor
public class JobController {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	
	@Autowired
	private SimpleJobOperator jobOperator;
	
	@Autowired
	private EmployeeRepository empRepo;
	
	@Autowired
	private TaskExecutor asyncTaskExecutor;
	
	@Autowired
	private JobInfoRepository jobRepo;
	
	@Autowired 
	private StepMasterRepo stepRepo;
	
	@Autowired
	private DuplicateEmpRepository duplicateEmpRepo;
	
//	@Autowired
//	private DuplicateEmployeeService service;
	
	@Autowired
	private StepCompletionListener stepListner;
	
	@Autowired
	private ExcelExportService excelExportService;
	
	private final BlockingQueue<JobParameters> jobQueue = new LinkedBlockingQueue<>();
	private final AtomicBoolean jobRunning = new AtomicBoolean(false);
	
	// Redirect to jonPage
	@GetMapping("/job")
	public ModelAndView showJobPage() {
		return new ModelAndView("jobPage");
	}
	
	// Processing the file 
	@PostMapping("/job-process")
    public ModelAndView csvToDb(@RequestParam("file") MultipartFile file) throws 
    JobExecutionAlreadyRunningException, JobRestartException, 
    JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        
		ModelAndView modelAndView = new ModelAndView("redirect:/job");

        if (file.isEmpty()) {
            modelAndView.addObject("message", "Please select a CSV file to upload.");
            return modelAndView;
        }
            
        // Save the uploaded file to a temporary location
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File tempFile = null;

        try {
            tempFile = File.createTempFile("temp-", fileName);
            file.transferTo(tempFile);
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startingAt", System.currentTimeMillis())
                    .addString("inputFilePath", tempFile.getAbsolutePath())
                    .toJobParameters();
            
            jobQueue.offer(jobParameters);

            modelAndView.addObject("message", "Job started successfully!");

        } catch (IOException e) {
            modelAndView.addObject("message", "File upload failed: " + e.getMessage());
            e.printStackTrace();
        }

        return modelAndView;
    }	
	
	// Scheduler that will run every 3 sec and check jobQueue if there is a job waiting or not
	@Scheduled(fixedDelay = 3000)
	public void runJobFromQueue() {
		if (!jobQueue.isEmpty() && jobRunning.compareAndSet(false, true)) {
			asyncTaskExecutor.execute(() -> {
				try {
					JobParameters jobParameters = jobQueue.poll();
					if (jobParameters != null) {
						jobLauncher.run(job, jobParameters);
					}
				} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
						| JobParametersInvalidException e) {
					e.printStackTrace();
				} finally {
					jobRunning.set(false);
				}
			});
		}
	}
	
	// Show duplicate employees
	@GetMapping("/duplicate")
	public ModelAndView duplicateEmployee() {
		ModelAndView modelAndView = new ModelAndView("duplicates");
		
		Long jobId = stepListner.getJobId();
		JobModel currJob = jobRepo.findById(jobId).get();
				
		List<DuplicateEmployeeModel> duplicates = duplicateEmpRepo.findByJob(currJob);
		modelAndView.addObject("duplicates", duplicates);
		
		return modelAndView;
	}
	
	// Download Excel 
	@GetMapping("/duplicates/excel")
    public ResponseEntity<InputStreamResource> downloadDuplicatesExcel() {
		
		Long jobId = stepListner.getJobId();
		JobModel currJob = jobRepo.findById(jobId).get();
				
		List<DuplicateEmployeeModel> duplicates = duplicateEmpRepo.findByJob(currJob);
		
        ByteArrayInputStream in = excelExportService.exportToExcel(duplicates);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=duplicates.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
	 
	 // process to terminate the job
	@PostMapping("/terminate")
    public ModelAndView terminateJob() {
        ModelAndView modelAndView = new ModelAndView("redirect:/batchInfo");

        try {
            Set<Long> runningExecutions = jobOperator.getRunningExecutions("job"); // Replace with your job name
            if (!runningExecutions.isEmpty()) {
                Long executionId = runningExecutions.iterator().next();
                jobOperator.stop(executionId);
                modelAndView.addObject("message", "Job terminated successfully!");
            } else {
                modelAndView.addObject("message", "No running jobs found to terminate.");
            }
        } catch (Exception e) {
            modelAndView.addObject("message", "Failed to terminate job: " + e.getMessage());
            e.printStackTrace();
        }

        return modelAndView;
    }
	
	// Redirect to all eployee page
	@GetMapping("/allEmp")
	public ModelAndView showEmp(Model model)
	{
		ModelAndView modelAndView = new ModelAndView("all_employee");
		List<EmployeeModel> all = empRepo.findAll(); 
		model.addAttribute("employee", all);
		
		 return modelAndView;
	}
	
	@GetMapping("/batchInfo")
	public ModelAndView showBatchProcessing(Model model) {
		
		ModelAndView modelAndView = new ModelAndView("batch_info");
		List<JobModel> allJob = jobRepo.findAll();
		List<StepMaster> allStep = stepRepo.findAll();
		model.addAttribute("alljob", allJob);
		model.addAttribute("allstep", allStep);
		
		return modelAndView;
	}
	
	@GetMapping("/jobs")
    public List<JobModel> getAllJobs() {
        return jobRepo.findAll();
    }

	 @GetMapping("/steps/{jobId}")
	    public List<StepMaster> getStepsByJobId(@PathVariable Long jobId) {

		 List<StepMaster> stepList = stepRepo.findByJobExecutionId(jobId);
		 
		 return stepList;
	}
	
}
