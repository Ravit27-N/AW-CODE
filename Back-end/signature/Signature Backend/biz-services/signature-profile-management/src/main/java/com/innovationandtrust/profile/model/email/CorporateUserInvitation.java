package com.innovationandtrust.profile.model.email;

import com.innovationandtrust.profile.constant.EmailTemplateConstant;
import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import org.thymeleaf.TemplateEngine;

public class CorporateUserInvitation extends EmailModel {

  private String password;

  private String token;

  public CorporateUserInvitation(
      String fullName,
      String email,
      String frontendLinkUrl,
      EmailTheme theme,
      String password,
      String token) {
    super(fullName, null, email, frontendLinkUrl, theme);
    this.password = password;
    this.token = token;
  }

  @Override
  public String getDefaultSubject() {
    return "Bienvenu sur votre espace " + this.getCompanyName();
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("password", password);
    context.setVariable("linkUrl", this.getLinkUrl() + "/activate-account/" + token);
    return templateEngine.process(EmailTemplateConstant.CORPORATE_USER_INVITATION, context);
  }
}
