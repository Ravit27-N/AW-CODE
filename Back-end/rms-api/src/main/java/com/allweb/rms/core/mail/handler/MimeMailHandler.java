package com.allweb.rms.core.mail.handler;

import com.allweb.rms.core.mail.GenericMailMessage;
import com.allweb.rms.core.mail.MailException;
import com.allweb.rms.core.mail.MailHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Setter
public class MimeMailHandler implements MailHandler {

  private String host;
  private String userName;
  private String password;
  private int port;
  private String protocol;
  private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

  @Override
  public void send(GenericMailMessage message) throws MailException {
    configEmailSender();
    try {
      mailSender.send(getSimpleMailMessage(message));
    } catch (org.springframework.mail.MailException | MessagingException e) {
      throw new MailException(e);
    }
  }

  private void configEmailSender() {
    mailSender.setHost(this.host);
    mailSender.setUsername(this.userName);
    mailSender.setPassword(this.password);
    mailSender.setPort(this.port);
    mailSender.setProtocol(this.protocol);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
  }

  private MimeMessage getSimpleMailMessage(GenericMailMessage genericMailMessage)
      throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    helper.setFrom(genericMailMessage.getFrom());
    helper.setText(genericMailMessage.getBody(), true);
    helper.setTo(genericMailMessage.getTo().toArray(new String[0]));
    if (genericMailMessage.getBcc() != null) {
      helper.setBcc(genericMailMessage.getBcc().toArray(new String[0]));
    }
    if (genericMailMessage.getCc() != null) {
      helper.setCc(genericMailMessage.getCc().toArray(new String[0]));
    }
    if (genericMailMessage.getReplyTo() != null) {
      helper.setReplyTo(genericMailMessage.getReplyTo());
    }
    if (genericMailMessage.getSentDate() != null) {
      helper.setSentDate(genericMailMessage.getSentDate());
    }
    if (genericMailMessage.getSubject() != null) {
      helper.setSubject(genericMailMessage.getSubject());
    }
    return message;
  }
}
