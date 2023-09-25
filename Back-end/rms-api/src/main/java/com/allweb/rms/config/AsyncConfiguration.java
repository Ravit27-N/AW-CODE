package com.allweb.rms.config;

import com.vladmihalcea.concurrent.aop.OptimisticConcurrencyControlAspect;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AsyncConfiguration implements AsyncConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);

  /*
  *
  * AsyncConfiguration  ConfigYYYY

  *
  * */
  @Override
  @Bean
  @Primary
  public Executor getAsyncExecutor() {
    LOGGER.debug("Creating Async Task Executor");
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("ThreadPoolTaskExecutor-");
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (ex, method, objects) -> {
      LOGGER.error("Throwable Exception message :{}", ex.getMessage());
      LOGGER.error("Method name :{}", method.getName());
      for (Object object : objects) {
        LOGGER.error("Object debug :{}", object);
      }
    };
  }

  @Bean
  public OptimisticConcurrencyControlAspect optimisticConcurrencyControlAspect() {
    return new OptimisticConcurrencyControlAspect();
  }
}
