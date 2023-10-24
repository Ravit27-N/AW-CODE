package com.innovationandtrust.configuration.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "share-id")
@Getter
@Setter
@AllArgsConstructor
public class ShareIdProperty {
  @JsonProperty("business_identifier")
  private String businessIdentifier;

  @JsonProperty("callback_url")
  private String callbackUrl;

  @JsonProperty("business_secret")
  private String businessSecret;
}
