package com.innovationandtrust.profile.model.email;

import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import com.innovationandtrust.utils.mail.model.MailRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class EmailModel implements IEmailInvitation {
  private String firstName;
  private String subject;
  @Getter private String email;
  private String linkUrl;
  private EmailTheme theme;

  protected Context getParamsContext() {
    var context = new Context(Locale.FRENCH);
    context.setVariable("firstName", this.firstName);
    context.setVariable("theme", getThemeColor());
    context.setVariable("companyName", this.getCompanyName());
    context.setVariable("linkUrl", this.linkUrl);
    return context;
  }

  public String getThemeColor() {
    return this.theme != null ? this.theme.getMainColor() : "#D6056A";
  }

  public String getCompanyName() {
    return Objects.nonNull(theme) && Objects.nonNull(theme.getCompanyName())
        ? this.theme.getCompanyName()
        : "Certigna";
  }

  @Override
  public String getDefaultSubject() {
    return null;
  }

  public String getSubject() {
    if (this.subject == null || this.subject.equals(Strings.EMPTY)) return this.getDefaultSubject();
    return this.subject;
  }

  public String getBody(TemplateEngine templateEngine) {
    return null;
  }

  public MailRequest getMailRequest(TemplateEngine templateEngine) {
    return new MailRequest(this.getEmail(), this.getSubject(), this.getBody(templateEngine));
  }
}
