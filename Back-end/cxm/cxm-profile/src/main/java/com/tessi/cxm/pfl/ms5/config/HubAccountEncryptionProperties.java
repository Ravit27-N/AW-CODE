package com.tessi.cxm.pfl.ms5.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "cxm.hub-account.encryption")
public class HubAccountEncryptionProperties {

  private int pageSize = 1000;
}
