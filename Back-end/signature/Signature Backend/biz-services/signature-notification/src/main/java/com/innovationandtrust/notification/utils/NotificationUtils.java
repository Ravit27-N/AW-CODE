package com.innovationandtrust.notification.utils;

import com.innovationandtrust.notification.model.email.EmailInvitationRequest;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.mail.provider.MailServiceProvider;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationUtils {
  public static MimeMessage getMessage(
      MailRequest request, Resource logo, MailServiceProvider mailServiceProvider) {
    if (logo == null) {
      return mailServiceProvider.prepareMimeMessage(
          request,
          Map.of(
              EmailInvitationRequest.LOGO_KEY,
              new ClassPathResource(EmailInvitationRequest.LOGO_PATH.toString())));
    }
    return mailServiceProvider.prepareMimeMessageAttachment(
        request, Map.of(EmailInvitationRequest.LOGO_KEY, logo), "image/png");
  }
}
