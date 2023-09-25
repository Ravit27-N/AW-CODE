package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WatermarkEnhancementPostalDeliveryPrivilege implements BaseEnumPrivilege {
  CREATE(setKey(ProfileConstants.Privilege.CREATE), "Create a watermark", true, false),
  MODIFY(setKey(ProfileConstants.Privilege.MODIFY), "Modify a watermark", false, true),
  DELETE(setKey(ProfileConstants.Privilege.DELETE), "Delete a watermark", false, true);
  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        WatermarkEnhancementPostalDeliveryPrivilege.class
            .getName()
            .concat(com.tessi.cxm.pfl.ms5.constant.ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(WatermarkEnhancementPostalDeliveryPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        WatermarkEnhancementPostalDeliveryPrivilege.class
            .getName()
            .concat(com.tessi.cxm.pfl.ms5.constant.ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(WatermarkEnhancementPostalDeliveryPrivilege.class, key));
  }

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY
        .concat("_".concat(subKey));
  }
}
