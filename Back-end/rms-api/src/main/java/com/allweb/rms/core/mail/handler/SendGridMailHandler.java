package com.allweb.rms.core.mail.handler;

import com.allweb.rms.core.mail.GenericMailMessage;
import com.allweb.rms.core.mail.MailException;
import com.allweb.rms.core.mail.MailHandler;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public class SendGridMailHandler implements MailHandler {

  @Setter private String apiKey;

  @Override
  public void send(GenericMailMessage message) throws MailException {
    Mail mail = this.getMail(message);
    SendGrid sendGrid = this.getSendGrid();
    try {
      Request request = new Request();
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sendGrid.api(request);
      if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
        throw new MailException(
            "Sendgrid failed to send an message with status " + response.getStatusCode() + ".");
      }
    } catch (IOException e) {
      throw new MailException(e.getMessage(), e);
    }
  }

  private SendGrid getSendGrid() {
    return new SendGrid(this.apiKey);
  }

  private Mail getMail(GenericMailMessage genericMailMessage) {
    Personalization personalization = new Personalization();
    if (genericMailMessage.getBcc() != null) {
      genericMailMessage.getBcc().stream()
          .filter(StringUtils::isNotEmpty)
          .map(Email::new)
          .forEach(personalization::addBcc);
    }
    if (genericMailMessage.getCc() != null) {
      genericMailMessage.getCc().stream()
          .filter(StringUtils::isNotEmpty)
          .map(Email::new)
          .forEach(personalization::addCc);
    }
    if (genericMailMessage.getTo() != null) {
      genericMailMessage.getTo().stream()
          .filter(StringUtils::isNotEmpty)
          .map(Email::new)
          .forEach(personalization::addTo);
    }
    Mail mail = new Mail();
    mail.setFrom(new Email(genericMailMessage.getFrom()));
    mail.setReplyTo(new Email(genericMailMessage.getReplyTo()));
    mail.setSendAt(
        genericMailMessage.getSentDate() != null
            ? genericMailMessage.getSentDate().toInstant().getEpochSecond()
            : (new Date()).toInstant().getEpochSecond());
    mail.setSubject(genericMailMessage.getSubject());
    mail.addContent(new Content("text/html", genericMailMessage.getBody()));

    mail.addPersonalization(personalization);
    return mail;
  }
}
