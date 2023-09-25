package com.allweb.rms.service.mail;

import com.allweb.rms.service.mail.config.SendGridConfig;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SendGridMailService implements EmailService {

  /*
   *
   * Send grid service
   *
   * */

  private final SendGridConfig sendGridConfig;

  @Autowired
  public SendGridMailService(SendGridConfig sendGrid) {
    this.sendGridConfig = sendGrid;
  }

  @Override
  public void sendHTML(String from, Set<String> to, Set<String> cc, String subject, String body) {
    Response response = send(from, to, cc, subject, new Content("text/html", body));
    log.debug(response.getStatusCode());
    log.debug(response.getHeaders());
    log.debug(response.getBody());
  }

  public Response send(
      String from, Set<String> to, Set<String> cc, String subject, Content content) {
    for (Set<String> strings : Arrays.asList(to, cc)) {
      strings.removeIf(String::isEmpty);
    }
    Personalization personalization = new Personalization();
    Mail mail = new Mail();
    for (String s : to) {
      personalization.addTo(new Email(s));
    }
    if (!cc.isEmpty()) cc.forEach(e -> personalization.addCc(new Email(e)));
    mail.setFrom(new Email(from));
    mail.addPersonalization(personalization);
    mail.setSubject(subject);
    mail.addContent(content);
    Response response = new Response();
    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    try {
      request.setBody(mail.build());
      response = sendGridConfig.sendGrid().api(request);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return response;
  }

  /**
   * Meth use to add Attachment To SendGrid
   *
   * @param mail object
   * @param fileDirectory optional
   */
  @SneakyThrows
  public void addAttachmentToSendGrid(Mail mail, String fileDirectory) {
    String fileExtension = FilenameUtils.getExtension(fileDirectory);
    String fileName = FilenameUtils.getName(fileDirectory);

    final InputStream inputStream = Files.newInputStream(Paths.get(fileDirectory));
    final Attachments attachments =
        new Attachments.Builder(fileName, inputStream)
            .withType(getContentType(fileExtension))
            .build();
    mail.addAttachments(attachments);
  }

  /*
   * get ContentType
   * */
  String getContentType(String fileExtension) {
    String contentType;

    switch (fileExtension) {
      case "pdf":
        contentType = "application/pdf";
        break;
      case "png":
        contentType = "image/png";
        break;
      case "jpeg":
      case "jpg":
        contentType = "image/jpeg";
        break;
      default:
        contentType = "application/octet-stream";
    }
    return contentType;
  }
}
