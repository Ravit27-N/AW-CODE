package com.innovationandtrust.configuration.datasource;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * For sharing common configuration of datasource.
 *
 * @since 25-April-2023
 * @version 1.0.0
 */
@Slf4j
@EnableTransactionManagement(proxyTargetClass = true)
public class CommonDatasourceConfiguration {

  private final DataSource dataSource;

  @Lazy
  public CommonDatasourceConfiguration(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Configure datasource transaction manager.
   *
   * @param entityManagerFactory refers to the object of {@link EntityManagerFactory}
   * @return object of {@link PlatformTransactionManager}
   */
  @Bean
  public PlatformTransactionManager transactionManager(
      final EntityManagerFactory entityManagerFactory) {
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
}
