package com.innovationandtrust.utils.schedule.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchedulerConstant {

  public static final String SCHEDULER_FACTORY_BEAN = "schedulerFactoryBean";
  public static final String SCHEDULER_JOB_FACTORY = "schedulerJobFactory";

  public static final String SCHEDULER_TRANSACTION_MANAGER = "schedulerTransactionManager";
  public static final String QUARTZ_DATASOURCE_BEAN_NAME = "quartzDatasource";
  public static final String QUARTZ_SCHEDULER_PROPERTY = "quartzSchedulerProperty";
  public static final String QUARTZ_DATASOURCE_PROPERTY = "quartzDatasourceProperty";
  public static final String APPLICATION_SCHEDULER_CONTEXT_KEY = "applicationSchedulerContextKey";
}
