package com.innovationandtrust.profile.model.email;

import com.innovationandtrust.profile.constant.EmailTemplateConstant;
import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.TemplateEngine;

@Setter
@Getter
public class ResetPasswordSuccess extends EmailModel {

  public ResetPasswordSuccess(
          String firstName,
          String email,
          EmailTheme theme) {
    super(firstName,null, email, null, theme);
  }

  @Override
  public String getDefaultSubject() {
    return "Reinitialisation de votre mot de passe";
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    return templateEngine.process(EmailTemplateConstant.RESET_PASSWORD_SUCCESS, this.getParamsContext());
  }
}
