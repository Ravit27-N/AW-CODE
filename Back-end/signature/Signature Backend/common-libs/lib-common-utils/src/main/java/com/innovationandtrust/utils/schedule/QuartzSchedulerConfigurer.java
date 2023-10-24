package com.innovationandtrust.utils.schedule;

import com.innovationandtrust.utils.schedule.constant.SchedulerConstant;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
public class QuartzSchedulerConfigurer {

  private final ApplicationContext context;

  protected QuartzSchedulerConfigurer(final ApplicationContext context) {
    this.context = context;
  }

  /**
   * {@link JobFactory} bean initialization.
   *
   * @return bean of {@link JobFactory}
   */
  @Bean(value = SchedulerConstant.SCHEDULER_JOB_FACTORY)
  public JobFactory jobFactory() {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(context);
    return jobFactory;
  }

  /**
   * Initial bean of {@link SchedulerFactoryBean}.
   *
   * @return bean of {@link SchedulerFactoryBean}
   */
  @Bean(value = SchedulerConstant.SCHEDULER_FACTORY_BEAN)
  public SchedulerFactoryBean schedulerFactoryBean(
      @Qualifier(SchedulerConstant.QUARTZ_SCHEDULER_PROPERTY) Properties quartProps,
      @Qualifier(SchedulerConstant.QUARTZ_DATASOURCE_BEAN_NAME) DataSource quartzDatasource,
      @Qualifier(SchedulerConstant.SCHEDULER_JOB_FACTORY) JobFactory jobFactory,
      @Qualifier(SchedulerConstant.SCHEDULER_TRANSACTION_MANAGER) PlatformTransactionManager transactionManager) {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    schedulerFactory.setQuartzProperties(quartProps);
    schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
    schedulerFactory.setAutoStartup(true);
    schedulerFactory.setDataSource(quartzDatasource);
    schedulerFactory.setJobFactory(jobFactory);
    schedulerFactory.setStartupDelay(5);
    schedulerFactory.setTransactionManager(transactionManager);
    schedulerFactory.setApplicationContextSchedulerContextKey(
        SchedulerConstant.APPLICATION_SCHEDULER_CONTEXT_KEY);
    return schedulerFactory;
  }

  /**
   * Initializing quartz properties from quartz.properties file.
   *
   * @return object of {@link Properties}
   */
  @Bean(SchedulerConstant.QUARTZ_SCHEDULER_PROPERTY)
  @ConfigurationProperties(prefix = "spring.quartz")
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(
        new ClassPathResource(
            "/quartz/quartz.properties", QuartzSchedulerConfigurer.class.getClassLoader()));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }

  @Bean(SchedulerConstant.QUARTZ_DATASOURCE_PROPERTY)
  @ConfigurationProperties(prefix = "spring.quartz.datasource")
  public DataSourceProperties quartzDatasourceProperties() {
    return new DataSourceProperties();
  }

  @ConfigurationProperties(prefix = "spring.quartz.datasource.hikari")
  @Bean(SchedulerConstant.QUARTZ_DATASOURCE_BEAN_NAME)
  @QuartzDataSource
  public DataSource quartzDatasource(
      @Qualifier(SchedulerConstant.QUARTZ_DATASOURCE_PROPERTY) DataSourceProperties dataSourceProperties) {
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  /**
   * Configure datasource transaction manager.
   *
   * @param entityManagerFactory refers to the object of {@link EntityManagerFactory}
   * @return object of {@link PlatformTransactionManager}
   */
  @Bean(SchedulerConstant.SCHEDULER_TRANSACTION_MANAGER)
  public PlatformTransactionManager transactionManager(
      final EntityManagerFactory entityManagerFactory,
      @Qualifier(SchedulerConstant.QUARTZ_DATASOURCE_BEAN_NAME) DataSource dataSource) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setValidateExistingTransaction(true);
    jpaTransactionManager.setDataSource(dataSource);
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
    return jpaTransactionManager;
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Bean
  @ConditionalOnClass(SchedulerFactoryBean.class)
  public SchedulerHandler schedulerHandler(
      @Qualifier(SchedulerConstant.SCHEDULER_FACTORY_BEAN) SchedulerFactoryBean schedulerFactoryBean) {
    return new SchedulerHandler(schedulerFactoryBean);
  }
}
