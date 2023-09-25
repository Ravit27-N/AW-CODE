package com.tessi.cxm.pfl.ms32.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Enumeration for metadata of flow document report meta data.
 */
public enum FlowDocumentReportMetaData {
  SUB_CHANNEL("flow-report.col.channel", "subChannel", "text"),
  VOLUME("flow-report.col.volume", "volume", "number"),
  TO_VALIDATE("flow-report.col.to-validate", "toValidate", "number"),
  SCHEDULE("flow-report.col.scheduled", "scheduled", "number"),
  IN_PROGRESS("flow-report.col.in-progress", "inProgress", "number"),
  OTHER("flow-report.col.other", "other", "number"),
  COMPLETED("flow-report.col.completed-percent", "completedPercentage", "percent");
  private String key;
  private String value;
  private String dataType;

  FlowDocumentReportMetaData(String key, String value, String dataType) {
    this.key = key;
    this.value = value;
    this.dataType = dataType;
  }

  private static Map<String, FlowDocumentReportMetaData> BY_KEY = new HashMap<>();
  private static Map<String, FlowDocumentReportMetaData> BY_VALUE = new HashMap<>();

  static {
    for (var metadata : values()) {
      BY_KEY.put(metadata.key.toLowerCase(Locale.ROOT), metadata);
      BY_VALUE.put(metadata.value.toLowerCase(Locale.ROOT), metadata);
    }
  }

  @JsonValue
  public String getKey() {
    return key;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonValue
  public String getDataType() {
    return dataType;
  }
}
