package com.tessi.cxm.pfl.ms3.config;

import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BatchContextConfig {
  @Bean
  public JobLauncherTestUtils jobLauncherTestUtils() {
    return new JobLauncherTestUtils() {
      @Override
      @Autowired
      public void setJob(
          @Qualifier(FlowTraceabilityConstant.NOTIFICATION_JOB_BEAN) Job notificationJob) {
        super.setJob(notificationJob);
      }
    };
  }

  @Bean
  public JobRepositoryTestUtils jobRepositoryTestUtils() {
    return new JobRepositoryTestUtils() {
      @Autowired JobLauncherTestUtils jobLauncherTestUtils;

      @Override
      @Autowired
      public void setJobRepository(JobRepository jobRepository) {
        super.setJobRepository(jobLauncherTestUtils.getJobRepository());
      }
    };
  }
}
