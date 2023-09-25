package com.allweb.rms.service.mail;

import com.allweb.rms.service.mail.config.GmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class GmailService implements EmailService {

  /*
   *
   * Class Service of MailConfig
   *
   * */

  private final GmailConfig sender;

  @Autowired
  public GmailService(GmailConfig sender) {
    this.sender = sender;
  }

  @Override
  public void sendHTML(String from, Set<String> to, Set<String> cc, String subject, String body) {
    for (Set<String> strings : Arrays.asList(cc, to)) {
      strings.removeIf(String::isEmpty);
    }
    send(from, to, cc, subject, body);
  }

  public void send(String from, Set<String> to, Set<String> cc, String subject, String body) {
    try {
      MimeMessage message = sender.javaMailSender().createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(String.valueOf(new InternetAddress(from)));
      helper.setTo(getStringArray(to));
      helper.setText(body, true);
      helper.setSubject(subject);
      helper.setSentDate(new Date());
      if (!cc.isEmpty()) helper.setCc(getStringArray(cc));

      sender.javaMailSender().send(message);
    } catch (MessagingException | MailException e) {
      // simply log it and go on...f
      log.error(e);
    }
  }

  public String[] getStringArray(Set<String> arrStr) {
    // convert Set String to String array
    return arrStr.toArray(new String[0]);
  }

  /**
   * Method use to add Attachment To Gmail
   *
   * @param helper Default MimeMessage encoding
   * @param fileDirectory optional
   */
  @SneakyThrows
  public void addAttachmentToGmail(MimeMessageHelper helper, String fileDirectory) {
    File file = new File(fileDirectory);
    FileSystemResource resource = new FileSystemResource(file);
    helper.addAttachment(file.getName(), resource);
  }
}
