package com.innovationandtrust.process.config;

import com.innovationandtrust.utils.schedule.QuartzSchedulerConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class SchedulerConfig extends QuartzSchedulerConfigurer {

  protected SchedulerConfig(ApplicationContext context) {
    super(context);
  }
}
