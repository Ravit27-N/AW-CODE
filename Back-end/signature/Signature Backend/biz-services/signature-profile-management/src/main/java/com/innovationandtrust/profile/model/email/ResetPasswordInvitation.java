package com.innovationandtrust.profile.model.email;

import com.innovationandtrust.profile.constant.EmailTemplateConstant;
import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.TemplateEngine;

@Setter
@Getter
public class ResetPasswordInvitation extends EmailModel {
  private Date expiryDate;
  private String resetToken;

  public ResetPasswordInvitation(
      String firstName,
      String email,
      String frontendLinkUrl,
      EmailTheme theme,
      String resetToken,
      Date expiryDate) {
    super(firstName, null, email, frontendLinkUrl, theme);
    this.expiryDate = expiryDate;
    this.resetToken = resetToken;
  }

  @Override
  public String getDefaultSubject() {
    return "Reinitialisation de votre mot de passe";
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("expiryDate", this.expiryDate);
    context.setVariable("linkUrl", this.getLinkUrl() + "/reset-password/" + resetToken);
    return templateEngine.process(EmailTemplateConstant.RESET_PASSWORD_INVITATION, context);
  }
}
