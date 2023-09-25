package com.tessi.cxm.pfl.ms11.constant;

import com.tessi.cxm.pfl.ms11.exception.ChannelMetaDataNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;


public enum ChannelMetadataType {

  SENDER_MAIL(ChannelMetaDataTypeConstant.SENDER_MAIL, "senderMail"),
  SENDER_NAME(ChannelMetaDataTypeConstant.SENDER_NAME, "senderName"),
  UNSUBSCRIBE_LINK(ChannelMetaDataTypeConstant.UNSUBSCRIBE_LINK, "unsubscribeLink"),
  SENDER_LABEL(ChannelMetaDataTypeConstant.SENDER_LABEL, "smsSenderLabel");

  private static final Map<String, ChannelMetadataType> STATIC_MAP = new HashMap<>();

  static {
    for (ChannelMetadataType f : values()) {
      STATIC_MAP.put(f.key.toLowerCase(), f);
    }
  }

  private final String key;
  private final String field;

  ChannelMetadataType(String key, String field) {
    this.key = key;
    this.field = field;
  }

  public String getKey() {
    return key;
  }

  public String getField() {
    return field;
  }

  public static ChannelMetadataType getByKey(String key) {
    if (ObjectUtils.isEmpty(STATIC_MAP.get(key.toLowerCase()))) {
      throw new ChannelMetaDataNotFoundException("Channel Metadata Type not found");
    }
    return STATIC_MAP.get(key.toLowerCase());
  }

  public static ChannelMetadataType getFieldByKey(String key) {
    return STATIC_MAP.get(key.toLowerCase(Locale.ROOT));
  }
}
