package com.innovationandtrust.profile.model.email;

import com.innovationandtrust.profile.constant.EmailTemplateConstant;
import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.TemplateEngine;

@Setter
@Getter
public class ConfirmChangeMail extends EmailModel {

  private String token;

  public ConfirmChangeMail(
      String fullName, String email, String frontendLinkUrl, EmailTheme theme, String token) {
    super(fullName, null, email, frontendLinkUrl, theme);
    this.token = token;
  }

  @Override
  public String getDefaultSubject() {
    return "Bienvenu sur votre espace " + this.getCompanyName();
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("linkUrl", this.getLinkUrl() + "/email-confirmation/" + token);
    return templateEngine.process(EmailTemplateConstant.CONFIRM_CHANG_MAIL, context);
  }
}
