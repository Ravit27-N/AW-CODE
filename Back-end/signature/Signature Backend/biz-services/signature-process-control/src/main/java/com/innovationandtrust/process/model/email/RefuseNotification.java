package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;

/** Mail request for refused process. */
public class RefuseNotification extends EmailInvitationModel {

  private final String fullName;
  private final String comment;

  @Setter private boolean isRefuser;

  @Setter private boolean isEndUser;

  /** Constructor for init super fields and required fields. */
  public RefuseNotification(
      EmailParametersModel emailParametersModel,
      String fullName,
      String comment,
      String companyName,
      String theme) {
    super(
        emailParametersModel.getFirstName(),
        emailParametersModel.getProjectName(),
        emailParametersModel.getMessage(),
        emailParametersModel.getSubject(),
        emailParametersModel.getLinkUrl(),
        emailParametersModel.getEmail(),
        companyName,
        theme
        );
    this.fullName = fullName;
    this.comment = comment;
  }

  @Override
  public String getDefaultSubject() {
    String subject = "Le(s) document(s) de projet a(ont) été refusé(s)";
    if (!isEndUser) {
      subject =
          isRefuser
              ? "Confirmation de refus de signer"
              : "Information au sujet de la signature du document " + super.getProjectName();
    }
    return subject;
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("fullName", this.fullName);
    context.setVariable("comment", this.comment);

    String template = InvitationTemplateConstant.EMAIL_REFUSE_TEMPLATE;
    if (!isEndUser) {
      template =
          isRefuser
              ? InvitationTemplateConstant.EMAIL_REFUSE_CONFIRM_TEMPLATE
              : InvitationTemplateConstant.EMAIL_REFUSE_INFORM_TEMPLATE;
    }

    return templateEngine.process(template, context);
  }
}
