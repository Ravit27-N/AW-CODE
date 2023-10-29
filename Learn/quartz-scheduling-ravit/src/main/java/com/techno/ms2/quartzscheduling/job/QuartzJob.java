package com.techno.ms2.quartzscheduling.job;

import com.techno.ms2.quartzscheduling.entity.CheckQuartz_;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class QuartzJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    var jobName = context.getJobDetail().getJobDataMap().get(CheckQuartz_.CHECK_NAME).toString();
    var jobId = context.getJobDetail().getJobDataMap().get(CheckQuartz_.ID).toString();
    log.info("Quartz job {} executed at {}", jobName + jobId, new Date());
  }
}
