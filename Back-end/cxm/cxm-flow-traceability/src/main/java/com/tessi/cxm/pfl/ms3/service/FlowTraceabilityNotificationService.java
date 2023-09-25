package com.tessi.cxm.pfl.ms3.service;

import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import java.nio.file.Path;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Handling process of receiving file after detection.
 *
 * @author Vichet CHANN
 */
@Log4j2
@Service
public class FlowTraceabilityNotificationService {

  private final FileService fileService;
  private final JobLauncher jobLauncher;

  private final Job notificationJob;

  public FlowTraceabilityNotificationService(
      FileService fileService,
      JobLauncher jobLauncher,
      @Qualifier(FlowTraceabilityConstant.NOTIFICATION_JOB_BEAN) Job notificationJob) {
    this.fileService = fileService;
    this.jobLauncher = jobLauncher;
    this.notificationJob = notificationJob;
  }

  /**
   * To launch process of notification after file detected.
   *
   * @param pathFile refers the full path of file after detected.
   */
  @Async
  public void launchNotification(Path pathFile) {
    log.info("Launching notification file path: {}", pathFile.toString());
    // decompress file after detect
    var xmlPath = this.storeDecompressFiles(pathFile);
    this.launchJobProcess(xmlPath);
  }

  private void launchJobProcess(Path xmlPath) {
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString(FlowTraceabilityConstant.XML_FILE_PATH, xmlPath.toString())
            .toJobParameters();

    try {
      JobExecution jobExecution = jobLauncher.run(notificationJob, jobParameters);
      log.info("Job execution status [ {} ]", jobExecution.getStatus());
    } catch (JobExecutionAlreadyRunningException
        | JobInstanceAlreadyCompleteException
        | JobRestartException
        | JobParametersInvalidException e) {
      log.error(e.getMessage(), e);
      if (log.isDebugEnabled()) {
        log.debug(e.getMessage(), e);
      }
    }
  }

  /**
   * Store decompress files.
   *
   * @param pathFile target file to decompress
   */
  private Path storeDecompressFiles(Path pathFile) {
    final Path xmlPath = this.fileService.getPath("xml", UUID.randomUUID().toString());
    this.fileService.deCompressGzFile(pathFile, xmlPath);
    // Remove file after extract
    fileService.deleteDirectoryQuietly(pathFile);
    log.info("xml file path: {}", xmlPath.toString());

    return xmlPath;
  }
}
