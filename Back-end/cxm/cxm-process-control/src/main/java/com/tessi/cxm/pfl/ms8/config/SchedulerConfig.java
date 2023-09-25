package com.tessi.cxm.pfl.ms8.config;

import com.tessi.cxm.pfl.shared.scheduler.QuartzSchedulerConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig extends QuartzSchedulerConfig {
  public SchedulerConfig(ApplicationContext context) {
    super(context);
  }
}
