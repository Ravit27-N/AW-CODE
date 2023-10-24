package com.innovationandtrust.utils.mail.provider;

import com.innovationandtrust.utils.mail.model.MailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

@Slf4j
public record MailServiceProvider(JavaMailSender mailSender, @Getter String sender) {

  public void sendMails(List<MailRequest> requests) {
    if (requests.size() == 1) {
      requests.stream().map(this::prepareMimeMessage).findFirst().ifPresent(this::sendMail);
      return;
    }
    this.mailSender.send(
        requests.stream().map(this::prepareMimeMessage).toList().toArray(new MimeMessage[] {}));
  }

  public void sendMail(MimeMessage message) {
    this.mailSender.send(message);
  }

  public void sendMail(MailRequest mailRequest) {
    this.mailSender.send(prepareMimeMessage(mailRequest));
  }

  public void sendBatchMails(List<MimeMessage> messages) {
    this.mailSender.send(messages.toArray(new MimeMessage[] {}));
  }

  private MimeMessage prepareMimeMessage(final MailRequest mailRequest) {
    MimeMessage message = mailSender.createMimeMessage();
    this.buildMimeMessageHelper(message, mailRequest, false);
    return message;
  }

  public MimeMessage prepareMimeMessage(
      final MailRequest mailRequest, Map<String, ClassPathResource> attachments) {
    var message = this.mailSender.createMimeMessage();

    var helper = this.buildMimeMessageHelper(message, mailRequest, true);
    if (!attachments.isEmpty()) {
      attachments.forEach(
          (key, value) -> {
            try {
              helper.addInline(key, value, "image/png");
            } catch (MessagingException e) {
              log.error("", e);
            }
          });
    }
    return message;
  }

  public MimeMessage prepareMimeMessageAttachment(
      final MailRequest mailRequest, Map<String, Resource> attachments, String contentType) {
    var message = this.mailSender.createMimeMessage();

    var helper = this.buildMimeMessageHelper(message, mailRequest, true);
    if (!attachments.isEmpty()) {
      attachments.forEach(
          (key, value) -> {
            try {
              helper.addInline(key, value, contentType);
            } catch (MessagingException e) {
              log.error("", e);
            }
          });
    }
    return message;
  }

  private MimeMessageHelper buildMimeMessageHelper(
      MimeMessage message, MailRequest request, boolean isMultipart) {
    try {
      MimeMessageHelper helper =
          new MimeMessageHelper(message, isMultipart, StandardCharsets.UTF_8.name());
      if (StringUtils.hasText(sender)) {
        helper.setFrom(sender);
      }
      if (Objects.nonNull(request.getTo())) {
        helper.setTo(request.getTo().toArray(new String[0]));
      }

      if (Objects.nonNull(request.getCc())) {
        helper.setCc(request.getCc().toArray(new String[0]));
      }

      helper.setSubject(request.getSubject());
      helper.setText(request.getBody(), true);
      helper.setSentDate(new Date());
      return helper;
    } catch (MessagingException e) {
      log.error("", e);
      throw new IllegalArgumentException("Failed to prepare mail message");
    }
  }
}
