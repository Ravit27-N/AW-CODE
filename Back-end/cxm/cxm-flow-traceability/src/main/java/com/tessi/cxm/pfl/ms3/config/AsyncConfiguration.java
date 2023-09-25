package com.tessi.cxm.pfl.ms3.config;

import com.tessi.cxm.pfl.shared.config.AbstractAsyncConfigure;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * This class handle asynchronous processing.
 *
 * @author Piseth KHON
 * @since 24/11/21
 * @see AbstractAsyncConfigure
 */
@Configuration
@EnableAsync
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AsyncConfiguration extends AbstractAsyncConfigure {

  /**
   * Initialize Object that superclass needed.
   *
   * @see AbstractAsyncConfigure
   */
  public AsyncConfiguration() {
    setCorePoolSize(5);
    setMaxPoolSize(10);
    setQueueCapacity(500);
    setThreadNamePrefix("CampaignThreadPoolTaskExecutor-");
  }
}
