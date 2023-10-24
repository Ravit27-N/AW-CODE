package com.innovationandtrust.utils.schedule.model;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

@Slf4j
@Getter
@AllArgsConstructor
public class CronTriggerDescriptor implements Serializable {

  private String id;

  private String group;

  private String expression;

  private Date endDate;

  private TimeZone timeZone;

  public CronTriggerDescriptor(String id, String group, String expression, Date endDate) {
    this(id, group, expression, endDate, TimeZone.getTimeZone(ZoneId.of("UTC")));
  }

  public Trigger buildTrigger(JobDetail jobDetail) {
    var startAt = new DateTimeSchedule(new Date());
    startAt.plus(Duration.ofSeconds(20));
    var trigger =
        TriggerBuilder.newTrigger()
            .withIdentity(this.id, this.group)
            .withSchedule(cronSchedule(this.expression)
                    .inTimeZone(this.timeZone)
                    .withMisfireHandlingInstructionFireAndProceed())
            .usingJobData(jobDetail.getJobDataMap())
            .startAt(startAt.toDate())
            .endAt(new DateTimeSchedule(this.endDate).toDate())
            .forJob(jobDetail);
    if (jobDetail.getKey() != null) {
      trigger.forJob(jobDetail.getKey());
    }
    return trigger.build();
  }
}
