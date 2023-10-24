package com.innovationandtrust.project.config;

import com.innovationandtrust.configuration.datasource.CommonDatasourceConfiguration;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class DataSourceConfig {

  @Bean
  @Lazy
  public CommonDatasourceConfiguration datasourceConfiguration(final DataSource dataSource) {
    return new CommonDatasourceConfiguration(dataSource);
  }
}
