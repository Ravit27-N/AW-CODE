package com.techno.ms2.quartzscheduling;

import com.techno.ms2.quartzscheduling.config.QuartzConfig;
import com.techno.ms2.quartzscheduling.job.SimpleJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
@Import({QuartzConfig.class})
public class QuartzSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzSchedulingApplication.class, args);
	}



}
