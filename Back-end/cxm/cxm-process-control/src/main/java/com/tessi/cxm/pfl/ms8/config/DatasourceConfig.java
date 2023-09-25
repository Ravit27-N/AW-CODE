package com.tessi.cxm.pfl.ms8.config;

import com.tessi.cxm.pfl.shared.config.datasource.JpaHibernateSchemaConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * The datasource configuration process-control service.
 *
 * @author Vichet CHANN
 * @version 1.15.0
 */
@Configuration
public class DatasourceConfig extends JpaHibernateSchemaConfig {

  public DatasourceConfig(
      @Value("${cxm.datasource.sequence.use-schema-as-prefix:true}") boolean useSchemaAsPrefix) {
    super(useSchemaAsPrefix);
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
