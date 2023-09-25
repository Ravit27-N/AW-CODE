package com.tessi.cxm.pfl.ms3.config;

import com.tessi.cxm.pfl.shared.config.datasource.JpaHibernateSchemaConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.jmx.JmxEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * The flow-traceability datasource configuration.
 *
 * @author Vichet CHANN
 */
@Configuration
@EnableAutoConfiguration(exclude = {JmxAutoConfiguration.class, JmxEndpointAutoConfiguration.class})
public class DatasourceConfig extends JpaHibernateSchemaConfig {

  public DatasourceConfig(
      @Value("${cxm.datasource.sequence.use-schema-as-prefix:true}") boolean useSchemaAsPrefix) {
    super(useSchemaAsPrefix);
  }
  @Bean
  public DefaultTaskConfigurer defaultTaskConfigurer(@Autowired DataSource dataSource) {
    return new DefaultTaskConfigurer(dataSource);
  }

  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSourceProperties datasourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  @Bean
  public DataSource datasource(@Autowired DataSourceProperties dataSourceProperties) {
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
        .build();
  }
}
