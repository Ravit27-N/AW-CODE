package com.allweb.rms.core.scheduler.job;

import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.support.ReminderJobData;
import com.allweb.rms.core.scheduler.support.SpringBatchJobData;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class ReminderBatchJobLauncher extends QuartzJobBean {

  private JobLauncher jobLauncher;
  private ApplicationContext applicationContext;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    try {
      this.init(context);

      ReminderJobData reminderJobData = new ReminderJobData(context.getMergedJobDataMap());
      SpringBatchJobData batchJobData = new SpringBatchJobData(context.getMergedJobDataMap());
      // Reminder id
      JobParameters jobParameters =
          new JobParametersBuilder()
              .addString(
                  ReminderConstants.REMINDER_ID_KEY,
                  String.valueOf(reminderJobData.getReminderId()))
              .addString(
                  ReminderConstants.USER_EMAIL_KEY, String.valueOf(reminderJobData.getUserEmail()))
              .addString(
                  ReminderConstants.ALLOW_REPORT_SENDING_STATE_KEY,
                  String.valueOf(batchJobData.allowReportSendingState()))
              .toJobParameters();

      Job reminderJob = applicationContext.getBean(batchJobData.getJobName(), Job.class);
      JobExecution jobExecution = jobLauncher.run(reminderJob, jobParameters);

      log.info("Quartz job launcher execution status: {}.", jobExecution.getStatus());
    } catch (SchedulerException
        | JobExecutionAlreadyRunningException
        | JobRestartException
        | JobInstanceAlreadyCompleteException
        | JobParametersInvalidException e) {
      log.debug(e.getMessage(), e);
    }
  }

  private void init(JobExecutionContext context) throws SchedulerException {
    applicationContext =
        (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
    this.jobLauncher = applicationContext.getBean(JobLauncher.class);
  }
}
