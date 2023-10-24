package com.innovationandtrust.process.model.email;

import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/** The email template attributes model for generating am email template with specific attribute. */
@AllArgsConstructor
public class EmailInvitationModel implements EmailInvitationRequestInterface {
  private String firstName;
  @Getter private String projectName;
  private String message;
  private String subject;
  private String linkUrl;
  @Getter private String email;
  private String companyName;
  private String theme;
  private Resource logo;

  protected Context getParamsContext() {
    var context = new Context(Locale.FRENCH);
    context.setVariable("firstName", this.firstName);
    context.setVariable("newProjectName", this.projectName.trim());
    context.setVariable("invitationMessage", this.getMessage());
    context.setVariable("theme", this.getTheme());
    context.setVariable("companyName", this.companyName);
    context.setVariable("linkUrl", this.linkUrl);
    context.setVariable("logo", logo);
    context.setVariable("BtnTextColor", "#ffffff");
    return context;
  }

  public String getDefaultSubject() {
    return null;
  }

  public String getDefaultMessage() {
    return null;
  }

  public String getDefaultTheme() {
    return null;
  }

  /** To retrieve the message to include in an email. */
  public String getMessage() {
    if (this.message == null || this.message.equals(Strings.EMPTY)) {
      return this.getDefaultMessage();
    }
    return this.message;
  }

  /** To retrieve the subject of the email. */
  public String getSubject() {
    if (this.subject == null || this.subject.equals(Strings.EMPTY)) {
      return this.getDefaultSubject();
    }
    return this.subject;
  }

  /** To receive the email template them for specific receiver. */
  public String getTheme() {
    if (this.theme == null || this.theme.equals(Strings.EMPTY)) {
      return this.getDefaultTheme();
    }
    return this.theme;
  }

  public String getBody(TemplateEngine templateEngine) {
    return null;
  }

  public MailRequest getMailRequest(TemplateEngine templateEngine) {
    return new MailRequest(this.getEmail(), this.getSubject(), this.getBody(templateEngine));
  }
}
