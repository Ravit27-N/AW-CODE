package com.innovationandtrust.profile.model.email;

import com.innovationandtrust.utils.mail.model.MailRequest;
import java.nio.file.Path;
import org.thymeleaf.TemplateEngine;

public interface IEmailInvitation {

  Path LOGO_PATH = Path.of("/static/images/logo.png");

  String LOGO_KEY = "logo";

  String getEmail();

  String getDefaultSubject();

  String getSubject();

  String getBody(TemplateEngine templateEngine);

  MailRequest getMailRequest(TemplateEngine templateEngine);
}
