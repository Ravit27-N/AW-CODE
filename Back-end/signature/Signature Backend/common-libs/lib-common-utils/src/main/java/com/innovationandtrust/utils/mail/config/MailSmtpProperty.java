package com.innovationandtrust.utils.mail.config;

import com.innovationandtrust.utils.mail.exception.MailException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "signature.mail")
public class MailSmtpProperty {

  @NotNull
  @NotEmpty(message = "The host is required and cannot be empty")
  private String host;

  private Integer port = 587;

  @NotNull
  @NotEmpty(message = "The username is required and cannot be empty")
  private String username;

  @NotNull
  @NotEmpty(message = "The password is required and cannot be empty")
  private String password;

  private String sender;

  private boolean enableTls = true;

  public boolean isValidSender() {
    if (!Objects.equals(this.host, "smtp.gmail.com") && !StringUtils.hasText(this.sender)) {
      throw new MailException("Sender must not empty");
    }
    return true;
  }
}
