package com.innovationandtrust.process.model.email;

import com.innovationandtrust.utils.mail.model.MailRequest;
import java.nio.file.Path;
import org.thymeleaf.TemplateEngine;

public interface EmailInvitationRequestInterface {

  Path LOGO_PATH = Path.of("/static/images/logo.png");

  String LOGO_KEY = "logo";

  String getEmail();

  String getSubject();

  String getBody(TemplateEngine templateEngine);

  MailRequest getMailRequest(TemplateEngine templateEngine);
}
