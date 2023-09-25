package com.allweb.rms.config;

import com.allweb.rms.constant.BeanNameConstant;
import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.batch.task.EmailSendingTasklet;
import com.allweb.rms.core.scheduler.batch.task.FirebaseNotificationSendingTasklet;
import com.allweb.rms.core.scheduler.batch.task.LoadReminderDetailTasklet;
import com.allweb.rms.core.scheduler.batch.task.ReminderReportTasklet;
import com.allweb.rms.core.scheduler.batch.task.ReminderValidationTasklet;
import com.allweb.rms.entity.jpa.MailTemplate;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.entity.jpa.ReminderType;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfiguration {

  private final DataSource dataSource;

  @Autowired
  protected BatchConfig(
      final ApplicationContext context,
      @Qualifier(BeanNameConstant.PRIMARY_DATA_SOURCE) DataSource dataSource) {
    this.setApplicationContext(context);
    this.dataSource = dataSource;
  }

  @Override
  protected DataSource getDataSource() {
    return this.dataSource;
  }

  ExecutionContextSerializer batchExecutionContextSerializer() {
    return new Jackson2ExecutionContextStringSerializer(
        Reminder.class.getName(),
        ReminderType.class.getName(),
        MailTemplate.class.getName(),
        Timestamp.class.getName());
  }

  // Step
  @Bean("loadReminderDetailStep")
  Step loadReminderDetailStep(LoadReminderDetailTasklet loadReminderDetailStep) {
    ExecutionContextPromotionListener executionContextPromotionListener =
        new ExecutionContextPromotionListener();
    executionContextPromotionListener.setKeys(new String[] {ReminderConstants.REMINDER_KEY});
    return new StepBuilder("loadReminderDetailStep", super.jobRepository())
        .tasklet(loadReminderDetailStep, super.getTransactionManager())
        .listener(executionContextPromotionListener)
        .build();
  }

  @Bean("reminderValidationStep")
  Step reminderValidationStep(ReminderValidationTasklet reminderValidationStep) {
    return new StepBuilder("reminderValidationStep", super.jobRepository())
        .tasklet(reminderValidationStep, super.getTransactionManager())
        .build();
  }

  @Bean("sendEmailStep")
  Step sendEmailStep(EmailSendingTasklet emailSendingTasklet) {
    ExecutionContextPromotionListener executionContextPromotionListener =
        new ExecutionContextPromotionListener();
    executionContextPromotionListener.setKeys(new String[] {ReminderConstants.EMAIL_TASK_DATA_MAP});
    return new StepBuilder("sendEmailStep", super.jobRepository())
        .tasklet(emailSendingTasklet, super.getTransactionManager())
        .listener(executionContextPromotionListener)
        .build();
  }

  @Bean("sendFirebaseNotificationStep")
  Step sendFirebaseNotificationStep(
      FirebaseNotificationSendingTasklet firebaseNotificationSendingTasklet) {
    ExecutionContextPromotionListener executionContextPromotionListener =
        new ExecutionContextPromotionListener();
    executionContextPromotionListener.setKeys(
        new String[] {ReminderConstants.FIREBASE_TASK_DATA_MAP});
    return new StepBuilder("sendFirebaseNotificationStep", super.jobRepository())
        .tasklet(firebaseNotificationSendingTasklet, super.getTransactionManager())
        .listener(executionContextPromotionListener)
        .build();
  }

  @Bean("reminderReportStep")
  Step reminderReportStep(ReminderReportTasklet reminderReportTasklet) {
    return new StepBuilder("reminderReportStep", super.jobRepository())
        .tasklet(reminderReportTasklet, super.getTransactionManager())
        .build();
  }

  // Flow

  @Bean("loadReminderDetailFlow")
  Flow loadReminderDetailFlow(
      @Qualifier("loadReminderDetailStep") Step loadReminderDetailStep,
      @Qualifier("reminderValidationStep") Step reminderValidationStep) {
    return new FlowBuilder<SimpleFlow>("loadReminderDetailFlow")
        .start(loadReminderDetailStep)
        .next(reminderValidationStep)
        .build();
  }

  @Bean("emailFlow")
  Flow emailFlow(@Qualifier("sendEmailStep") Step sendEmailStep) {
    return new FlowBuilder<SimpleFlow>("emailFlow").start(sendEmailStep).build();
  }

  @Bean("firebaseFlow")
  Flow firebaseFlow(@Qualifier("sendFirebaseNotificationStep") Step sendFirebaseNotificationStep) {
    return new FlowBuilder<SimpleFlow>("firebaseFlow").start(sendFirebaseNotificationStep).build();
  }

  @Bean("parallelNotificationFlow")
  Flow parallelNotificationFlow(
      @Qualifier("emailFlow") Flow emailFlow, @Qualifier("firebaseFlow") Flow firebaseFlow) {
    return new FlowBuilder<SimpleFlow>("parallelNotificationFlow")
        .split(taskExecutor())
        .add(emailFlow, firebaseFlow)
        .build();
  }

  TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor("spring-batch-reminder");
  }

  // Job
  @Bean(ReminderConstants.BATCH_REMINDER)
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  Job reminderJob(
      JobRepository jobRepository,
      @Qualifier("loadReminderDetailFlow") Flow loadReminderDetailFlow,
      @Qualifier("parallelNotificationFlow") Flow parallelNotificationFlow,
      @Qualifier("reminderReportStep") Step reminderReportStep) {

    return new JobBuilder(
            ReminderConstants.BATCH_REMINDER + "-" + System.currentTimeMillis(), jobRepository)
        .preventRestart()
        .start(loadReminderDetailFlow)
        .on(ExitStatus.COMPLETED.getExitCode())
        .to(parallelNotificationFlow)
        .on(ExitStatus.FAILED.getExitCode())
        .to(reminderReportStep)
        .from(parallelNotificationFlow)
        .next(reminderReportStep)
        .end()
        .build();
  }
}
