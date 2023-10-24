package com.innovationandtrust.profile.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "signature.super-admin")
public class SuperAdminProperty {
  @NotEmpty private String email;
  private String firstName;
  private String lastName;
  @NotEmpty private String password;
}
