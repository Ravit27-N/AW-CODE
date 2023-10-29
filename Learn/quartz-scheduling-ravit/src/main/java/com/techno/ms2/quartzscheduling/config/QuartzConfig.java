package com.techno.ms2.quartzscheduling.config;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableScheduling
public class QuartzConfig {
  private final ApplicationContext applicationContext;
  private final DataSource datasource;
  private final JobFactory jobFactory;

  @Autowired
  public QuartzConfig(
      @Lazy ApplicationContext applicationContext,
      @Lazy JobFactory jobFactory,
      DataSource datasource) {
    this.applicationContext = applicationContext;
    this.jobFactory = jobFactory;
    this.datasource = datasource;
  }

  @Bean
  public JobFactory jobFactory() {
    AutowiringSpringBeanJobFactory factory = new AutowiringSpringBeanJobFactory();
    factory.setApplicationContext(applicationContext);
    return factory;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    schedulerFactory.setQuartzProperties(quartzProperties());
    schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
    schedulerFactory.setAutoStartup(true);
    schedulerFactory.setDataSource(datasource);
    schedulerFactory.setJobFactory(jobFactory);
    schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
    return schedulerFactory;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }
}
