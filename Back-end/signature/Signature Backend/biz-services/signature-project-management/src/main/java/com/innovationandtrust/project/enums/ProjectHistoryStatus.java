package com.innovationandtrust.project.enums;

import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.InvitationStatus;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum ProjectHistoryStatus {
  CREATED("Created"),
  SENT("Sent"),
  SIGNED("Signed"),
  APPROVED("Approved"),
  COMPLETED("Completed");

  private static final Map<String, ProjectHistoryStatus> BY_VALUE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_VALUE.put(v.getStringVal(), v));
  }

  private final String stringVal;

  ProjectHistoryStatus(String value) {
    this.stringVal = value;
  }

  public static ProjectHistoryStatus getByDocumentStatus(DocumentStatus status) {
    switch (status) {
      case SIGNED -> {
        return SIGNED;
      }
      case APPROVED -> {
        return APPROVED;
      }
      default -> throw new IllegalArgumentException("Unable to map status");
    }
  }

  public static ProjectHistoryStatus getByInvitationStatus(InvitationStatus status) {
    switch (status) {
      case SIGNED -> {
        return SIGNED;
      }
      case SENT -> {
        return SENT;
      }
      default -> throw new IllegalArgumentException("Unable to map status");
    }
  }

  public ProjectHistoryStatus getByValue(String strValue) {
    return BY_VALUE.get(strValue);
  }
}
