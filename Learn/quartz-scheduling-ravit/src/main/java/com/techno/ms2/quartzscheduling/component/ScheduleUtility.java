package com.techno.ms2.quartzscheduling.component;

import com.techno.ms2.quartzscheduling.config.QuartzConfig;
import com.techno.ms2.quartzscheduling.job.QuartzJob;
import com.techno.ms2.quartzscheduling.entity.CheckQuartz;
import com.techno.ms2.quartzscheduling.entity.CheckQuartz_;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleUtility {
  private final QuartzConfig quartzConfig;

  public void schedule(CheckQuartz checkQuartz) throws ParseException {
    try {
      String quartzJobId = String.valueOf(checkQuartz.getId());

      // Job data
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(CheckQuartz_.ID, quartzJobId);
      jobDataMap.put(CheckQuartz_.CHECK_NAME, checkQuartz.getCheckName());

      JobDetail quartzJob =
          JobBuilder.newJob(QuartzJob.class).setJobData(jobDataMap).withIdentity(quartzJobId).build();

      Trigger trigger =
          TriggerBuilder.newTrigger()
              .withIdentity("Trigger" + quartzJobId)
              .withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?")) // every second
              // .withSchedule(
              //     SimpleScheduleBuilder.simpleSchedule()
              //         .withRepeatCount(0)
              //         .withIntervalInSeconds(1))
              .build();

      Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();
      scheduler.start();
      scheduler.scheduleJob(quartzJob, trigger);
    } catch (SchedulerException | IOException e) {
      e.printStackTrace();
    }
  }
}
