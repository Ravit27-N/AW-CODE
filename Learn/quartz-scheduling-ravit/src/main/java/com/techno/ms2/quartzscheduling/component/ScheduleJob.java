package com.techno.ms2.quartzscheduling.component;

import com.techno.ms2.quartzscheduling.config.QuartzConfig;
import com.techno.ms2.quartzscheduling.dto.CandidateDto;
import com.techno.ms2.quartzscheduling.job.CandidateJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleJob {

    private final QuartzConfig quartzConfig;
    @Autowired
    private Scheduler scheduler;

    public void schedule(CandidateDto dto){

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 3);
            Date newDate = calendar.getTime();

            JobDetail jobDetail = buildJobDetail(dto);
            Trigger trigger = buildJobTrigger(jobDetail, newDate);
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Job Create Successfully");
        } catch (SchedulerException ex) {
            log.error("Error scheduling: ", ex);
        }

    }


    private JobDetail buildJobDetail(CandidateDto dto) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("id", dto.getId().toString());
        jobDataMap.put("name", dto.getName());

        return JobBuilder.newJob(CandidateJob.class)
                .withIdentity(UUID.randomUUID().toString(), "candidate-jobs")
                .withDescription("Set Status to false")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


}
