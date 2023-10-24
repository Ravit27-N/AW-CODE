package com.innovationandtrust.configuration.datasource.multitenant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class TenantContext {

  public static final String DEFAULT_TENANT_ID = "public";
  private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

  public static String getCurrentTenant() {
    return currentTenant.get();
  }

  public static void setCurrentTenant(String tenant) {
    currentTenant.set(tenant);
  }

  public static void clear() {
    currentTenant.remove();
  }
}
