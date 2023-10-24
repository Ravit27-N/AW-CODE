package com.innovationandtrust.share.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "signature.setting")
public class SettingProperties {
  @NotNull(message = "Urgent project cannot be empty")
  private Integer urgentProject;
}
