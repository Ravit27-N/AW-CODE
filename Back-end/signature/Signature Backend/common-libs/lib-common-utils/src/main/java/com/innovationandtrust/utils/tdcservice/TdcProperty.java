package com.innovationandtrust.utils.tdcservice;

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
@ConfigurationProperties(prefix = "signature.tdc-service", ignoreInvalidFields = true)
public class TdcProperty {
  private String url;
  
  private String tenantId;

  private String account;
  
  private String password;

  private String baseId;

}
