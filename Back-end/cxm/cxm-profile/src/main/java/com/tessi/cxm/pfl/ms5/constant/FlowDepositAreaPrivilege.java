package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.FlowDepositArea;
import lombok.AllArgsConstructor;

/**
 * This enumeration is used to define the privileges of {@link FlowDepositAreaPrivilege}
 *
 * @author Sokhour LACH
 * @since 03/01/2022
 */
@AllArgsConstructor
public enum FlowDepositAreaPrivilege implements BaseEnumPrivilege {
  SUBMIT(setKey(FlowDepositArea.SUBMIT), "Submit a feed", false, false),
  CHOOSE_SHIPPING_CHANNEL(
      setKey(FlowDepositArea.CHOOSE_SHIPPING_CHANNEL), "Choose the shipping channel", false, false),
  SELECT_PRODUCTION_CRITERIA(
      setKey(FlowDepositArea.SELECT_PRODUCTION_CRITERIA),
      "Select production criteria",
      false,
      false),

  // new privileges for v0.5 (03/10/22)
  SEND_A_LETTER(setKey(FlowDepositArea.SEND_A_LETTER), "Send a letter", false, false),
  LIST_DEPOSITS(setKey(FlowDepositArea.LIST_DEPOSITS), "List deposits", true, false),
  MODIFY_A_DEPOSIT(setKey(FlowDepositArea.MODIFY_A_DEPOSIT), "Modify a deposit", false, true),
  DELETE_A_DEPOSIT(setKey(FlowDepositArea.DELETE_A_DEPOSIT), "Delete a deposit", false, true),
  MODIFY_OR_CORRECT_AN_ADDRESS(setKey(FlowDepositArea.MODIFY_OR_CORRECT_AN_ADDRESS), "Modify or correct an address", false, true);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerLevelValidator(
        FlowDepositAreaPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(FlowDepositAreaPrivilege.class, key));
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public boolean isVisibility() {
    return isVisibility;
  }

  @Override
  public boolean isModification() {
    return isModification;
  }

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_DEPOSIT.concat(
        "_".concat(subKey));
  }
}
