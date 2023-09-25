package com.tessi.cxm.pfl.ms11.constant;

import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum ClientSettingFunctionalities {
  EMAILING_CAMPAIGN(ProfileConstants.CXM_CAMPAIGN, "Portal/CampaignEmail/csv", "Email"),
  BATCH("batch_email", "Batch/zip", "Email"),
  SMS_CAMPAIGN(ProfileConstants.CXM_SMS_CAMPAIGN, "Portal/CampaignSms/csv", "Sms"),
  IV("IV", "IV/pdf", ""),
  CXM_FLOW_DEPOSIT(ProfileConstants.CXM_FLOW_DEPOSIT, "Portal/pdf", "");
  private final String key;
  private final String value;
  private final String subValue;
  private static final Map<String, ClientSettingFunctionalities> STATIC_MAP = new HashMap<>();

  static {
    for (ClientSettingFunctionalities f : values()) {
      STATIC_MAP.put(f.key.toLowerCase(), f);
    }
  }

  public String getValue() {
    return value;
  }

  public String getSubValue() {
    return subValue;
  }

  public String getPrefixClientValue(String clientName) {
    return String.format("%s/%s", clientName, value);
  }

  public String getDepositType() {
    return value.split("/")[0];
  }

  public String getExtension() {
    String[] values = value.split("/");
    return values[values.length - 1];
  }

  public String getKey() {
    return key;
  }

  public static ClientSettingFunctionalities getByKey(String key) {
    return STATIC_MAP.get(key.toLowerCase());
  }
}
