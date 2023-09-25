package com.allweb.rms.config;

import com.allweb.rms.constant.BeanNameConstant;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.logging.Logger;

@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
public class DatasourceConfig {

  @Bean(BeanNameConstant.QUARTZ_DATA_SOURCE_PROPERTY)
  @ConfigurationProperties("spring.quartz.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(BeanNameConstant.QUARTZ_DATA_SOURCE)
  @QuartzDataSource
  @ConfigurationProperties("spring.quartz.datasource.hikari")
  public DataSource dataSource(
      @Qualifier(BeanNameConstant.QUARTZ_DATA_SOURCE_PROPERTY) DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  @Bean(BeanNameConstant.PRIMARY_DATA_SOURCE_PROPERTY)
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties defaultDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean(BeanNameConstant.PRIMARY_DATA_SOURCE)
  @ConfigurationProperties("spring.datasource.hikari")
  public DataSource defaultDatasource(
      @Qualifier(BeanNameConstant.PRIMARY_DATA_SOURCE_PROPERTY) DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

    @Component
    public static class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

      private static final Logger logger =
          Logger.getLogger(DatasourceProxyBeanPostProcessor.class.getName());

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) {

        if (bean instanceof DataSource dataSource) {

          logger.info(() -> "DataSource bean has been found: " + beanName);

          final ProxyFactory proxyFactory = new ProxyFactory(bean);

          proxyFactory.setProxyTargetClass(true);
          proxyFactory.addAdvice(new ProxyDataSourceInterceptor(dataSource, beanName));

          return proxyFactory.getProxy();
        }
        return bean;
      }

      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
      }

      public static class ProxyDataSourceInterceptor implements MethodInterceptor {

        private final DataSource dataSource;

        private ProxyDataSourceInterceptor(final DataSource dataSource, final String beanName) {
          this.dataSource =
              ProxyDataSourceBuilder.create(dataSource)
                  .name(beanName)
                  .logQueryBySlf4j(SLF4JLogLevel.INFO)
                  .multiline()
                  .build();
        }

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {

          final Method proxyMethod =
              ReflectionUtils.findMethod(this.dataSource.getClass(), invocation.getMethod().getName());

          if (proxyMethod != null) {
            return proxyMethod.invoke(this.dataSource, invocation.getArguments());
          }

          return invocation.proceed();
        }
      }
    }
}
