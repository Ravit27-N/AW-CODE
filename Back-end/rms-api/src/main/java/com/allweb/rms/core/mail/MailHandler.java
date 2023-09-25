package com.allweb.rms.core.mail;

public interface MailHandler {
  void send(GenericMailMessage message) throws MailException;
}
