package com.allweb.rms.core.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.Setter;

public class GenericMailMessage {
  private final List<String> bccList = new ArrayList<>();
  private final List<String> ccList = new ArrayList<>();
  private final List<String> receiverList = new ArrayList<>();
  @Setter private String from;
  @Setter private String replyTo;
  @Setter private Date sendDate;
  @Setter private String subject;
  @Setter private String body;

  public List<String> getCc() {
    return Collections.unmodifiableList(this.ccList);
  }

  public List<String> getBcc() {
    return Collections.unmodifiableList(this.bccList);
  }

  public List<String> getTo() {
    return Collections.unmodifiableList(this.receiverList);
  }

  public void setTo(String to) {
    this.receiverList.add(to);
  }

  public String getBody() {
    return this.body;
  }

  public String getFrom() {
    return this.from;
  }

  public String getReplyTo() {
    return this.replyTo;
  }

  public Date getSentDate() {
    return this.sendDate;
  }

  public String getSubject() {
    return this.subject;
  }
}
