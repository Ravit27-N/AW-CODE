package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.Statistic;
import lombok.AllArgsConstructor;

/**
 * This enumeration is used to define the privileges of {@link StatisticReportAreaPrivilege}
 *
 * @author Sokhour LACH
 * @since 03/01/2022
 */
@AllArgsConstructor
public enum StatisticReportAreaPrivilege implements BaseEnumPrivilege {
  // privileges of statistics
  GENERATE_STATISTIC(
      setKey(Statistic.GENERATE_STATISTIC), "Generate statistics (graphs)", true, false),
  DOWNLOAD_STATISTIC(
      setKey(Statistic.DOWNLOAD_STATISTIC), "Download statistical files", false, false);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerLevelValidator(
        StatisticReportAreaPrivilege.class
            .getName()
            .concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(StatisticReportAreaPrivilege.class, key));
  }

  /**
   * set value to key of {@link StatisticReportAreaPrivilege}
   *
   * @param subKey refer to constants key
   * @return string key value of {@link StatisticReportAreaPrivilege}
   */
  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_STATISTIC_REPORT.concat(
        "_".concat(subKey));
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
}
