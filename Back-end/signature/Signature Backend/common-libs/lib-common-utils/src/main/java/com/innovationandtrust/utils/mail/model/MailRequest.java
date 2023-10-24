package com.innovationandtrust.utils.mail.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest implements Serializable {

  @NotBlank(message = "`to` is required")
  @NotEmpty
  private Set<String> to;

  @NotBlank(message = "cc is required")
  private Set<String> cc;

  @NotBlank(message = "subject is required")
  private String subject;

  @NotBlank(message = "text or html test is required")
  private String body;

  public MailRequest(Set<String> to, String subject, String body) {
    this.to = to;
    this.subject = subject;
    this.body = body;
  }

  public MailRequest(String to, String subject, String body) {
    this(Collections.singleton(to), subject, body);
  }
}
