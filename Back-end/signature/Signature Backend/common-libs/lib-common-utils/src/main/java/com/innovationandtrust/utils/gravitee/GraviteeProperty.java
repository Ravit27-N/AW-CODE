package com.innovationandtrust.utils.gravitee;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "signature.gravitee", ignoreInvalidFields = true)
public class GraviteeProperty {
  private String username;
  private String password;
  private String environment;
  private String organizations;
  private String baseUrl;
  private String apiContextPath;
  private String version;
  private String status;
  private int page;
  private int pageSize;
  private String exclude;
  @Getter @Setter private List<String> applicationIds;
}
