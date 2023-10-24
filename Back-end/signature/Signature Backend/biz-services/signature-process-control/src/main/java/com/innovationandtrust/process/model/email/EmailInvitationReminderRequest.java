package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import com.innovationandtrust.process.constant.MessageConstant;
import com.innovationandtrust.process.utils.DateUtil;
import java.util.Date;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;

public class EmailInvitationReminderRequest extends EmailInvitationModel {
  private final Date expireDate;
  private final boolean isApproval;

  public EmailInvitationReminderRequest(
      EmailParametersModel emailParametersModel,
      String companyName,
      String theme,
      Resource logo,
      Date expireDate,
      boolean isApproval) {
    super(
        emailParametersModel.getFirstName(),
        emailParametersModel.getProjectName(),
        emailParametersModel.getMessage(),
        emailParametersModel.getSubject(),
        emailParametersModel.getLinkUrl(),
        emailParametersModel.getEmail(),
        companyName,
        theme,
        logo);
    this.expireDate = expireDate;
    this.isApproval = isApproval;
  }

  @Override
  public String getDefaultSubject() {
    return isApproval
        ? MessageConstant.APPROVAL_INVITE_REMINDER_SUBJECT
        : MessageConstant.SIGNER_INVITE_REMINDER_SUBJECT;
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("expireDate", DateUtil.toFrenchDate(this.expireDate));
    context.setVariable("dateOffset", DateUtil.getOffsetOfDate(this.expireDate));
    var template =
        isApproval
            ? InvitationTemplateConstant.APPROVE_REMINDER_TEMPLATE
            : InvitationTemplateConstant.SIGN_REMINDER_TEMPLATE;
    return templateEngine.process(template, context);
  }
}
