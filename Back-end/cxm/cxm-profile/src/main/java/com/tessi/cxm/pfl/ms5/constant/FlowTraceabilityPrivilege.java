package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.FlowDocument;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.FlowTraceability;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.SubCancelFlowTraceability;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of privileges of <b>Flow Traceability</b>
 *
 * @author Pisey CHORN
 * @author Sokhour LACH
 * @since 22/12/2021
 */
@Slf4j
@AllArgsConstructor
public enum FlowTraceabilityPrivilege implements BaseEnumPrivilege {
  // Flow Traceability or Flow monitoring
  FLOW_TRACEABILITY(setKey(Privilege.LIST), "View flow tracking", true, false),
  SELECT_FLOW_TRACEABILITY(
      setKey(FlowTraceability.SELECT_AND_OPEN),
      "Select a feed to open the document tracking",
      true,
      false),
  CANCEL_FLOW(setKey(SubCancelFlowTraceability.FLOW), "Cancel a flow", false, true),
  VALIDATE(setKey(FlowTraceability.VALIDATE), "Validate a flow", false, true),
  FINALIZE(
      setKey(FlowTraceability.FINALIZE),
      "Finalize a deposit (return to the deposit area)",
      false,
      true),
  DOWNLOAD(setKey(FlowTraceability.DOWNLOAD), "Download the feed", true, false),
  VIEW_EVENT_HISTORY(
      setKey(FlowTraceability.VIEW_EVENT_AND_HISTORY), "View event the history", true, false),

  // Flow Document tracking
  VIEW_DOCUMENT(setKey(FlowDocument.VIEW_DOCUMENT), "View document tracking", true, false),
  SELECT_OPEN_DOCUMENT(
      setKey(FlowDocument.SELECT_AND_OPEN), "Select a document to open the details", true, false),
  DOWNLOAD_DOCUMENT(setKey(FlowDocument.DOWNLOAD_DOCUMENT), "Download a document", true, false),
  VIEW_EVENT_HISTORY_DOCUMENT(
      setKey(FlowDocument.VIEW_EVENT_HISTORY), "View event history a document", true, false),
  OPEN_DOWNLOAD_DOCUMENT(
      setKey(FlowDocument.OPEN_AND_DOWNLOAD), "Open and download related items", true, false),
  VALIDATE_DOCUMENT(setKey(FlowDocument.VALIDATE), "Validate a document", false, true);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        FlowTraceabilityPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(FlowTraceabilityPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        FlowTraceabilityPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(FlowTraceabilityPrivilege.class, key));
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public boolean isVisibility() {
    return isVisibility;
  }

  public boolean isModification() {
    return isModification;
  }

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_TRACEABILITY.concat(
        "_".concat(subKey));
  }
}
