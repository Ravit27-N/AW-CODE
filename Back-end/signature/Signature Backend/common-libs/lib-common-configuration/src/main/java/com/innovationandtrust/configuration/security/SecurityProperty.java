package com.innovationandtrust.configuration.security;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "signature.security")
public class SecurityProperty {
  @NotNull @NotEmpty private String keycloakRealm;

  @NotNull @NotEmpty private String keycloakBaseUrl;

  @Getter @Setter private List<String> origins;
}
