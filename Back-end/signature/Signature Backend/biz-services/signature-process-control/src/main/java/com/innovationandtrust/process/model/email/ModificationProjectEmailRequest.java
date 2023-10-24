package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import com.innovationandtrust.process.constant.MessageConstant;
import com.innovationandtrust.process.utils.DateUtil;
import java.util.Date;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;

/** Mail request class storing data for mail template. */
public class ModificationProjectEmailRequest extends EmailInvitationModel {

  private final Date newExpireDate;

  private final boolean isApproval;

  /** Mail class constructor. */
  public ModificationProjectEmailRequest(
      EmailParametersModel emailParametersModel,
      String companyName,
      String theme,
      Resource logo,
      Date newExpireDate,
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
    this.newExpireDate = newExpireDate;
    this.isApproval = isApproval;
  }

  @Override
  public String getDefaultSubject() {
    return isApproval
        ? MessageConstant.APPROVAL_INFORM_MODIFY_EXP
        : MessageConstant.SIGNER_INFORM_MODIFY_EXP;
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("newExpireDate", DateUtil.toFrenchDate(this.newExpireDate));
    context.setVariable("dateOffset", DateUtil.getOffsetOfDate(this.newExpireDate));
    context.setVariable("isApprover", this.isApproval);
    return templateEngine.process(
        InvitationTemplateConstant.MODIFICATION_PROJECT_TEMPLATE, context);
  }
}
