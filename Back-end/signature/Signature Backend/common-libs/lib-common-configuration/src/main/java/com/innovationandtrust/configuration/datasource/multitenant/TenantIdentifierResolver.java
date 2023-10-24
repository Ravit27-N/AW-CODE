package com.innovationandtrust.configuration.datasource.multitenant;

import java.util.Map;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

public class TenantIdentifierResolver
    implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

  private String currentTenant = "public";

  public void setCurrentTenant(String currentTenant) {
    this.currentTenant = currentTenant;
  }

  @Override
  public String resolveCurrentTenantIdentifier() {
    return this.currentTenant;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
  }
}
