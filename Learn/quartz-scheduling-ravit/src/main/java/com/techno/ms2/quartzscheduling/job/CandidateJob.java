package com.techno.ms2.quartzscheduling.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CandidateJob extends QuartzJobBean {


        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

                JobDataMap jobDataMap = context.getMergedJobDataMap();
                String candidateId = jobDataMap.getString("id");

                log.info("Candidate ID: {}",candidateId);

        }
}
