package com.tessi.cxm.pfl.ms5.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Functionality.
 *
 * @author Sokhour LACH
 */
@Schema(enumAsRef = true)
public enum Functionality {
  TRACKING_SPACE(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_TRACEABILITY,
      "Tracking space",
      EnumUtils.getKeyValue(FlowTraceabilityPrivilege.class)),
  DESIGN_EMAILING_TEMPLATE(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_TEMPLATE_MODEL,
      "Design of an emailing template",
      EnumUtils.getKeyValue(EmailingTemplatePrivilege.class)),
  DESIGN_SMS_TEMPLATE(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_SMS_TEMPLATE,
      "Design of an sms template",
      EnumUtils.getKeyValue(SmsTemplatePrivilege.class)),
  EMAILING_CAMPAIGN(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_CAMPAIGN,
      "Management of an emailing campaign",
      EnumUtils.getKeyValue(EmailingCampaignPrivilege.class)),
  SMS_CAMPAIGN(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_SMS_CAMPAIGN,
      "Management of an sms campaign",
      EnumUtils.getKeyValue(SmsCampaignPrivilege.class)),
  USER_MANAGEMENT(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT,
      "User management",
      EnumUtils.getKeyValue(UserManagementPrivilege.class)),
  CXM_FLOW_DEPOSIT(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_DEPOSIT,
      "Flow deposit area",
      EnumUtils.getKeyValue(FlowDepositAreaPrivilege.class)),
  CXM_SETTING_UP_DOCUMENT_TEMPLATE(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_SETTING_UP_DOCUMENT_TEMPLATE,
      "Parameterization of the documentary models",
      EnumUtils.getKeyValue(SettingUpDocumentTemplatePrivilege.class)),
  CXM_STATISTIC_REPORT(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_STATISTIC_REPORT,
      "Statistics and reports area",
      EnumUtils.getKeyValue(StatisticReportAreaPrivilege.class)),
  CXM_DIRECTORY(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_DIRECTORY_MANAGEMENT,
      "Directory management",
      EnumUtils.getKeyValue(DirectoryManagementPrivilege.class)),
  CXM_ESPACE_VALIDATION(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_ESPACE_VALIDATION,
      "Espace validation",
      EnumUtils.getKeyValue(EspaceValidationPrivilege.class)),
  CXM_CLIENT_MANAGEMENT(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_CLIENT_MANAGEMENT,
      "Client management",
      EnumUtils.getKeyValue(ClientManagementPrivilege.class)),
  CXM_MANAGEMENT_LIBRARY_RESOURCE(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
      "Management library resource",
      EnumUtils.getKeyValue(ManagementLibraryResourcePrivilege.class)),
  CXM_INTERACTIVE(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_COMMUNICATION_INTERACTIVE,
      "Interactive communication",
      EnumUtils.getKeyValue(CommunicationInteractivePrivilege.class)),
  CXM_ENRICHMENT_MAILING(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_ENRICHMENT_MAILING,
      "Enrichment mailing",
      EnumUtils.getKeyValue(EnrichmentMailingPrivilege.class)),

  CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY(
      com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY,
      "Watermark enhancement postal delivery",
      EnumUtils.getKeyValue(WatermarkEnhancementPostalDeliveryPrivilege.class));

  // Watermark enhancement postal delivery
  private final String key;
  private final String value;
  private final Object subFunctionality;
  private static final Map<String, Functionality> BY_KEY = new HashMap<>();

  Functionality(String key, String value, Object subFunctionality) {
    this.key = key;
    this.value = value;
    this.subFunctionality = subFunctionality;
  }

  static {
    for (Functionality f : values()) {
      BY_KEY.put(f.value.toLowerCase(Locale.ROOT), f);
    }
  }

  @JsonValue
  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public Object getSubFunctionality() {
    return subFunctionality;
  }

  /**
   * Load key value of {@link Functionality}
   *
   * @return return list of {@link Map} with {@link String} keys and {@link String} values.
   */
  public static List<Map<String, Object>> getKeyValues() {
    List<Map<String, Object>> keyValues = new ArrayList<>();
    Arrays.stream(Functionality.values())
        .forEach(
            v ->
                keyValues.add(
                    Map.of(
                        ProfileConstants.OBJECT_KEY,
                        v.key,
                        ProfileConstants.OBJECT_VALUE,
                        v.value,
                        ProfileConstants.OBJECT_SUB_VALUE,
                        v.subFunctionality != null ? v.subFunctionality : new ArrayList<>())));
    return keyValues;
  }

  /**
   * Load key value of {@link Functionality}
   *
   * @return return list of {@link Map} with {@link String} keys and {@link String} values.
   */
  public static List<Map<String, Object>> getKeyValuesByKey(String key) {
    var filteredFunctionality =
        Arrays.stream(Functionality.values())
            .filter(v -> v.key.equalsIgnoreCase(key))
            .findFirst()
            .orElse(null);
    if (filteredFunctionality == null) {
      return new ArrayList<>();
    }

    return List.of(
        Map.of(
            ProfileConstants.OBJECT_KEY,
            filteredFunctionality.key,
            ProfileConstants.OBJECT_VALUE,
            filteredFunctionality.value,
            ProfileConstants.OBJECT_SUB_VALUE,
            filteredFunctionality.subFunctionality != null
                ? filteredFunctionality.subFunctionality
                : new ArrayList<>()));
  }

  public static Functionality valuesByKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  public static boolean keyExists(String key, boolean ignoreCase) {
    return Arrays.stream(Functionality.values())
        .anyMatch(
            functionality ->
                ignoreCase
                    ? functionality.getKey().equalsIgnoreCase(key)
                    : functionality.getKey().equals(key));
  }
}
