package com.tessi.cxm.pfl.ms32.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum GlobalProductionDetailMetaData {
  CHANNEL("cxm_analytics.global_table.channel", "channel", "text"),
  VOLUME_RECEIVED("cxm_analytics.global_table.volume_received", "volumeReceived", "number"),
  PND_MND("cxm_analytics.global_table.pnd_mnd", "pndMnd", "number"),
  PND_MND_PERCENTAGE("cxm_analytics.global_table.pnd_mnd_percentage", "pndMndPercentage",
      "percent"),
  TREATY("cxm_analytics.global_table.treaty", "treaty", "number"),
  IN_PROGRESS("cxm_analytics.global_table.in_progress", "inProgress", "number"),
  TREATY_PERCENTAGE("cxm_analytics.global_table.treaty_percentage", "treatyPercentage", "percent");
  private String key;
  private String value;
  private String dataType;

  private static Map<String, GlobalProductionDetailMetaData> BY_KEY = new HashMap<>();
  private static Map<String, GlobalProductionDetailMetaData> BY_VALUE = new HashMap<>();

  GlobalProductionDetailMetaData(String key, String value, String dataType) {
    this.key = key;
    this.value = value;
    this.dataType = dataType;
  }

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
