package com.innovationandtrust.sftp.model;

import com.innovationandtrust.sftp.constant.EmailTemplateConstant;
import com.innovationandtrust.utils.mail.model.MailRequest;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;

@Slf4j
public class ErrorEmail extends EmailModel {

  public ErrorEmail(String email, String corporateUuid, String subject, String message) {
    super(email, corporateUuid, subject, message);
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    return templateEngine.process(EmailTemplateConstant.ERROR_TEMPLATE, context);
  }

  @Override
  public MailRequest getMailRequest(TemplateEngine templateEngine) {
    return new MailRequest(this.getEmail(), this.getSubject(), this.getBody(templateEngine));
  }
}
