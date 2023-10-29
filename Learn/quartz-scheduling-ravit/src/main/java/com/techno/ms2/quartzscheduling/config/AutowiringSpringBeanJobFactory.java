package com.techno.ms2.quartzscheduling.config;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Slf4j
@RequiredArgsConstructor
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

  private AutowireCapableBeanFactory beanFactory;

  @Override
  public void setApplicationContext(final ApplicationContext context) {
    beanFactory = context.getAutowireCapableBeanFactory();
  }

  @Override
  protected @Nonnull Object createJobInstance(final @Nonnull TriggerFiredBundle bundle)
      throws Exception {
    final Object job = super.createJobInstance(bundle);
    log.info("AutowiringSpringBeanJobFactory: create job instance");
    beanFactory.autowireBean(job);
    return job;
  }
}
