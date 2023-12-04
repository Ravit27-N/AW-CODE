package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.EmailDefaultValue;
import com.innovationandtrust.utils.date.DateUtil;
import java.util.Date;
import java.util.Objects;
import lombok.Setter;
import org.thymeleaf.TemplateEngine;

public class EmailInvitationRequest extends EmailInvitationModel {

  @Setter private Date expireDate;

  @Setter private String role;

  @Setter private String fullName;

  public EmailInvitationRequest(
      EmailParametersModel emailParametersModel, String companyName, String theme) {
    super(
        emailParametersModel.getFirstName(),
        emailParametersModel.getProjectName(),
        emailParametersModel.getMessage(),
        emailParametersModel.getSubject(),
        emailParametersModel.getLinkUrl(),
        emailParametersModel.getEmail(),
        companyName,
        theme);
  }

  @Override
  public String getDefaultSubject() {
    return EmailDefaultValue.getSubject(role);
  }

  @Override
  public String getDefaultMessage() {
    return EmailDefaultValue.getMessage(role);
  }

  @Override
  public String getDefaultTheme() {
    return EmailDefaultValue.getTheme(role);
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    if (Objects.nonNull(expireDate)) {
      context.setVariable("expireDate", DateUtil.toFrenchDate(this.expireDate));
      context.setVariable("dateOffset", DateUtil.getOffsetOfDate(this.expireDate));
    }

    context.setVariable("fullName", this.fullName);
    return templateEngine.process(EmailDefaultValue.getTemplate(role), context);
  }
}
