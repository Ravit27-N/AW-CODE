package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.CampaignDetail;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.CreateCampaign;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.ListCampaign;
import lombok.AllArgsConstructor;

/**
 * Enumeration of privileges of <b>Emailing Campaign</b>.
 *
 * @author Sokhour LACH
 * @see 17/12/2021
 */
@AllArgsConstructor
public enum EmailingCampaignPrivilege implements BaseEnumPrivilege {
  // list of campaigns
  TRACKING_CAMPAIGN(setKey(Privilege.LIST), "Tracking campaigns", true, false),
  SELECT_CAMPAIGN(
      setKey(ListCampaign.SELECT_AND_VIEW), "Select a campaign to open the details", true, false),
  CANCEL_CAMPAIGN(setKey(ListCampaign.CANCEL), "Cancel a campaign", false, true),
  FINALIZE_CAMPAIGN(setKey(ListCampaign.FINALIZE), "Finalize a campaign", false, true),
  CREATE_CAMPAIGN(
      setKey(Privilege.CREATE),
      "Create a campaign (enables the following sub-feature privileges)",
      false,
      false),

  // campaign details
  VIEW_EMAIL(setKey(CampaignDetail.VIEW_EMAIL), "view the email", false, false),
  VIEW_CONTACTS(setKey(CampaignDetail.VIEW_CONTACTS), "view the contacts", false, false),

  // create a campaign.
  CHOOSE_MODEL(setKey(CreateCampaign.CHOOSE_MODEL), "Choose a model from the list", true, false),
  SEND_PROOF(setKey(CreateCampaign.TEST_SEND_MAIL), "Send a proof", false, false);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        EmailingCampaignPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(EmailingCampaignPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        EmailingCampaignPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(EmailingCampaignPrivilege.class, key));
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

  /**
   * set value to key of {@link EmailingCampaignPrivilege}
   *
   * @param subKey refer to constants key of {@link CreateCampaign}
   * @return string key value of {@link EmailingCampaignPrivilege}
   */
  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_CAMPAIGN.concat("_".concat(subKey));
  }
}
