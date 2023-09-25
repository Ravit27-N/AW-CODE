package com.tessi.cxm.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cxm")
@Data
public class MicroserviceContextPathConfigurationProperties {
  private List<MicroserviceDetails> services;

  @Data
  public static class MicroserviceDetails {
    private String name;
    private String contextPath;
  }
}
