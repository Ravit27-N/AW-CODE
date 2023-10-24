package com.innovationandtrust.sftp.model;

import com.innovationandtrust.utils.mail.model.MailRequest;
import java.nio.file.Path;
import org.thymeleaf.TemplateEngine;

public interface ErrorEmailRequest {
  Path LOGO_PATH = Path.of("/static/images/logo.png");

  String LOGO_KEY = "logo";

  String getEmail();

  String getSubject();

  String getBody(TemplateEngine templateEngine);

  MailRequest getMailRequest(TemplateEngine templateEngine);
}
