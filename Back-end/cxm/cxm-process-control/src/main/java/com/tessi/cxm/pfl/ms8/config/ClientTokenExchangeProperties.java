package com.tessi.cxm.pfl.ms8.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "cxm.auth.token-exchange.client")
public class ClientTokenExchangeProperties {

  private String clientId;
  private String clientSecret;
}
