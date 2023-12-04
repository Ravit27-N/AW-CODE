package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import org.thymeleaf.TemplateEngine;

public class FailedValidatePhoneMailModel extends EmailInvitationModel {
  public FailedValidatePhoneMailModel(
      EmailParametersModel emailParametersModel, String companyName, String theme) {
    super(
        emailParametersModel.getFirstName(),
        emailParametersModel.getProjectName(),
        null,
        emailParametersModel.getSubject(),
        null,
        emailParametersModel.getEmail(),
        companyName,
        theme);
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("theme", this.getTheme());
    return templateEngine.process(InvitationTemplateConstant.FAILED_VALIDATE_PHONE, context);
  }
}
