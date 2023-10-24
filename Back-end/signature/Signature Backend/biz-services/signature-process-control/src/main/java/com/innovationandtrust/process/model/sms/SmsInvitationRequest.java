package com.innovationandtrust.process.model.sms;

import com.innovationandtrust.process.utils.ProcessControlUtils;
import java.util.Date;
import org.thymeleaf.TemplateEngine;

public class SmsInvitationRequest extends SmsInvitationModel {

  public SmsInvitationRequest(
      String firstName,
      String projectName,
      String invitationMessage,
      Date expireDate,
      String linkUrl) {
    super(firstName, projectName, invitationMessage, expireDate, linkUrl);
  }

  public String getMessage(TemplateEngine templateEngine, String templateName) {
    return ProcessControlUtils.convertHtmlToString(
        templateEngine.process(templateName, super.getParamsContext()));
  }
}
