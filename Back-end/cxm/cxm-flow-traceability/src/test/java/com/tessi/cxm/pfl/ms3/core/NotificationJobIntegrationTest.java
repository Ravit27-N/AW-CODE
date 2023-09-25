package com.tessi.cxm.pfl.ms3.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tessi.cxm.pfl.ms3.config.BatchContextConfig;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {BatchContextConfig.class})
@Slf4j
class NotificationJobIntegrationTest {

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired private JobRepositoryTestUtils jobRepositoryTestUtils;

  private JobExecution jobExecution;

  @BeforeEach
  void setUp() {
    assertNotNull(this.jobLauncherTestUtils, "jobLauncherTestUtils should not be null");
    assertNotNull(this.jobRepositoryTestUtils, "jobRepositoryTestUtils should not be null");
  }

  @AfterEach
  void cleanUp() {
    this.jobRepositoryTestUtils.removeJobExecutions(Collections.singletonList(this.jobExecution));
    log.info("Starting clean test data in database....");
  }

  @Test
  void launchJob() throws Exception {

    // when
    JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(getJobParameters());
    String actualJobInstance = jobExecution.getJobInstance().getJobName().split("-")[0];
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
    this.jobExecution = jobExecution;

    // then
    Assertions.assertEquals(FlowTraceabilityConstant.NOTIFICATION_JOB, actualJobInstance);
    Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
  }

  private JobParameters getJobParameters() throws IOException {
    return new JobParametersBuilder()
        .addLong("jobId", System.currentTimeMillis())
        .addString("data", UUID.randomUUID().toString())
        .addLong("time", System.currentTimeMillis())
        .addString(
            FlowTraceabilityConstant.XML_FILE_PATH,
            new ClassPathResource("test.xml").getFile().getAbsolutePath())
        .toJobParameters();
  }
}
