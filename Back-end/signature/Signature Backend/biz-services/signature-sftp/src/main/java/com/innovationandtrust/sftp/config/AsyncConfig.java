package com.innovationandtrust.sftp.config;

import com.innovationandtrust.sftp.constant.FileIntegrationConstant;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

  @Bean(value = FileIntegrationConstant.ASYNC_TASK_EXECUTOR_BEAN)
  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    var numOfThread = Thread.activeCount();
    log.info("Total thread: {}", numOfThread);
    taskExecutor.setCorePoolSize((int) Math.ceil((double) numOfThread / 3));
    taskExecutor.setMaxPoolSize((int) Math.ceil((double) numOfThread / 2));
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
    taskExecutor.afterPropertiesSet();
    return taskExecutor;
  }
}
