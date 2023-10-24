package com.innovationandtrust.sftp.model;

import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
@Builder
@Getter
public class EmailModel implements ErrorEmailRequest {
  private String email;
  private String corporateUuid;
  private String subject;
  private String message;

  protected Context getParamsContext() {
    var context = new Context(Locale.FRENCH);
    context.setVariable("corporateUuid", this.corporateUuid);
    context.setVariable("message", this.message);
    context.setVariable("email", this.email);
    context.setVariable("subject", this.subject);
    return context;
  }

  @Override
  public String getEmail() {
    return this.email;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    return null;
  }

  @Override
  public MailRequest getMailRequest(TemplateEngine templateEngine) {
    return null;
  }
}
