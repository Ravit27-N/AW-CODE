package com.allweb.rms.utils;

import java.util.Arrays;
import java.util.List;

public enum SystemConfigurationConstants {
  MAIL_PROVIDER("mail.provider"),
  MAIL_SENDER("mail.sender"),
  MAIL_SERVER("mail.gmail.host"),
  MAIL_PROTOCOL("mail.gmail.protocol"),
  PORT("mail.gmail.port"),
  USERNAME("mail.gmail.username"),
  PASSWORD("mail.gmail.password"),
  API_KEY("mail.sendgrid.api.key"),
  SENDGRID("SENDGRID"),
  MAIL_TEMPLATE_ID("mail.template.id"),
  /** Must not end with "/". */
  BASE_PATH("path.base"),

  /** Must start with "/". */
  INTERVIEW_PATH("path.interview"),

  /** Must start with "/". */
  CANDIDATE_PATH("path.candidate"),

  DATE_TIME_FORMAT("datetime.format"),

  GPA("dashboard.gpa");

  private final String value;

  SystemConfigurationConstants(String value) {
    this.value = value;
  }

  public static List<String> getEnumList() {
    return Arrays.asList(
        MAIL_PROTOCOL.getValue(),
        MAIL_SERVER.getValue(),
        MAIL_PROVIDER.getValue(),
        MAIL_SENDER.getValue(),
        USERNAME.getValue(),
        PASSWORD.getValue(),
        PORT.getValue(),
        API_KEY.getValue(),
        MAIL_TEMPLATE_ID.getValue(),
        BASE_PATH.getValue(),
        INTERVIEW_PATH.getValue(),
        CANDIDATE_PATH.getValue(),
        DATE_TIME_FORMAT.getValue(),
        GPA.getValue());
  }

  public String getValue() {
    return value;
  }
}
