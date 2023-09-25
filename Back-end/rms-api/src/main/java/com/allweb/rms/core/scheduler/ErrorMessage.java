package com.allweb.rms.core.scheduler;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessage {
  protected List<String> messages = new ArrayList<>();

  @Override
  public String toString() {
    return String.join(",", messages);
  }

  public boolean isEmpty() {
    return messages.isEmpty();
  }

  public List<String> getMessages() {
    return messages;
  }

  public void addErrorMessage(String message) {
    this.messages.add(message);
  }

  public void addErrorMessages(List<String> messages) {
    this.messages.addAll(messages);
  }

  public void clear() {
    this.messages.clear();
  }
}
