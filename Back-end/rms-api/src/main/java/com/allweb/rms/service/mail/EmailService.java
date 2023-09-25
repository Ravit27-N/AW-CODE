package com.allweb.rms.service.mail;

import java.util.Set;
import javax.mail.MessagingException;

public interface EmailService {
  void sendHTML(String from, Set<String> to, Set<String> cc, String subject, String body)
      throws MessagingException;
}
