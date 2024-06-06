package com.demo.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

	@Bean("asyncTaskExecutor")
	public TaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setQueueCapacity(300);
		taskExecutor.setMaxPoolSize(8);
//		taskExecutor.setKeepAliveSeconds(60);
//		taskExecutor.setAllowCoreThreadTimeOut(true);
		taskExecutor.setThreadNamePrefix("AsyncTaskThread");
		taskExecutor.initialize();

		return taskExecutor;
	}

}
 