package com.allweb.rms.service;

import com.allweb.rms.core.scheduler.SchedulerHandler;
import com.allweb.rms.core.scheduler.job.ReminderBatchJobLauncher;
import com.allweb.rms.core.scheduler.model.DateTimeInfo;
import com.allweb.rms.core.scheduler.model.JobDetailDescriptor;
import com.allweb.rms.core.scheduler.model.JobTriggerDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.Duration;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock
    Scheduler scheduler;

    @Mock
    SchedulerFactoryBean schedulerFactoryBean;

    @InjectMocks
    SchedulerHandler schedulerHandler;

    @Captor
    ArgumentCaptor<JobDetail> jobDetailArgumentCaptor;

    @Captor
    ArgumentCaptor<Trigger> jobTriggerArgumentCaptor;

    @BeforeEach
    void init() {
        Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
        //schedulerHandler = new SchedulerHandler(schedulerFactoryBean);
    }

    @Test
    void testQuartzSchedule() throws SchedulerException {
        // create a job
        String jobId = "test-job-1";
        String jobGroup = "REMINDER";
        JobDetailDescriptor jobDescriptor = new JobDetailDescriptor(jobId, jobGroup, ReminderBatchJobLauncher.class);
        // create a Trigger to fire a job at a specified date & time
        String triggerId = "reminder-trigger";
        String triggerGroup = "REMINDER";
        DateTimeInfo triggeringInfo = new DateTimeInfo(LocalDateTime.now());
        triggeringInfo.plus(Duration.ofSeconds(30));

        JobTriggerDescriptor jobTriggerDescriptor = new JobTriggerDescriptor(triggerId, triggerGroup);
        jobTriggerDescriptor.triggerOn(triggeringInfo);
        Mockito.when(scheduler.checkExists(jobDescriptor.getJobKey())).thenReturn(false);
        Mockito.when(scheduler.checkExists(jobTriggerDescriptor.getTriggerKey())).thenReturn(false);
        // Schedule a new Job
        this.schedulerHandler.scheduleJob(jobTriggerDescriptor, jobDescriptor);
        //
        Mockito.verify(scheduler, Mockito.times(1)).scheduleJob(jobDetailArgumentCaptor.capture(), jobTriggerArgumentCaptor.capture());

        org.hamcrest.MatcherAssert.assertThat(jobDetailArgumentCaptor.getValue(), org.hamcrest.Matchers.notNullValue());
    }
}
