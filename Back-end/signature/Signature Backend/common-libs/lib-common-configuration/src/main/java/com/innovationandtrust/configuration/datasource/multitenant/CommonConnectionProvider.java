package com.innovationandtrust.configuration.datasource.multitenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

public class CommonConnectionProvider
    implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
  
  private final transient DataSource dataSource;

  public CommonConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Connection getAnyConnection() throws SQLException {
    return getConnection(TenantContext.DEFAULT_TENANT_ID);
  }

  @Override
  public void releaseAnyConnection(Connection connection) throws SQLException {
    connection.close();
  }

  @Override
  public Connection getConnection(String schema) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      connection.setSchema(schema);
      return connection;
    }
  }

  @Override
  public void releaseConnection(String schema, Connection connection) throws SQLException {
    connection.setSchema(schema);
    connection.close();
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return false;
  }

  @Override
  public boolean isUnwrappableAs(Class<?> aClass) {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> aClass) {
    return null;
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
  }
}
