package com.innovationandtrust.utils.feignclient;

import java.util.HashMap;
import java.util.Map;
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
@ConfigurationProperties(prefix = "signature.feign-client", ignoreInvalidFields = true)
public class FeignClientProperty {

  private String clientRegistrationId;

  private String clientId;

  private boolean enabled = false;

  private final Map<String, String> clients = new HashMap<>();
  private final Map<String, String> contexts = new HashMap<>();
}
