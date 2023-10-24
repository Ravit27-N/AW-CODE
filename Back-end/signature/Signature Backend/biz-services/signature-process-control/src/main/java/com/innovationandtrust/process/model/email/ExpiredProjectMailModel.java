package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;

public class ExpiredProjectMailModel extends EmailInvitationModel {
  public ExpiredProjectMailModel(
      String fullName,
      String projectName,
      String subject,
      String email,
      String companyName,
      String theme,
      Resource logo) {
    super(fullName, projectName, null, subject, null, email, companyName, theme, logo);
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("theme", this.getTheme());
    context.setVariable("BtnTextColor", "#ffffff");
    return templateEngine.process(InvitationTemplateConstant.PROJECT_EXPIRED_NOTI, context);
  }
}
