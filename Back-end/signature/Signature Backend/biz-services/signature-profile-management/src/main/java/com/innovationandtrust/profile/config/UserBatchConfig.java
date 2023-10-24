package com.innovationandtrust.profile.config;

import com.innovationandtrust.profile.component.UserProcessor;
import com.innovationandtrust.profile.component.UserReader;
import com.innovationandtrust.profile.component.UserWriter;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
@Slf4j
public class UserBatchConfig {
  private JobRepository jobRepository;

  private PlatformTransactionManager transactionManager;

  @Bean
  @StepScope
  public FlatFileItemReader<NormalUserDto> reader(
      @Value("#{jobParameters[sourceFilePath]}") String csvPath) {
    var userReader = new UserReader();
    userReader.setSourcePath(csvPath);
    return userReader.read();
  }

  @Bean
  public UserProcessor processor() {
    return new UserProcessor();
  }

  @Bean
  public ItemWriter<NormalUserDto> writer() {
    return new UserWriter();
  }

  @Bean("step1")
  public Step step1(FlatFileItemReader<NormalUserDto> flatFileItemReader) {
    return new StepBuilder("db-step", jobRepository)
        .<NormalUserDto, NormalUserDto>chunk(10, transactionManager)
        .reader(flatFileItemReader)
        .processor(processor())
        .writer(writer())
        .faultTolerant()
        .noRollback(Exception.class)
        .build();
  }

  @Bean
  public Job runJob(@Qualifier("step1") Step step1) {
    return new JobBuilder("importUsers", jobRepository)
        .preventRestart()
        .start(step1)
        .build();
  }

  @Bean
  public TaskExecutor taskExecutor() {
    SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
    asyncTaskExecutor.setConcurrencyLimit(5);
    return asyncTaskExecutor;
  }
}
