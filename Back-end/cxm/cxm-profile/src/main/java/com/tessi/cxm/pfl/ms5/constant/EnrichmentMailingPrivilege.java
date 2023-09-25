package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.EnrichmentMailing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EnrichmentMailingPrivilege implements BaseEnumPrivilege{


    ADD_RESOURCE(setKey(EnrichmentMailing.ADD_RESOURCE), "Add resource", true,
            false),
    USE_RESOURCE_IN_LIBRARY(setKey(EnrichmentMailing.USE_RESOURCE_IN_LIBRARY), "Use resource", true,
            false),
    UPLOAD_A_SINGLE_RESOURCE(setKey(EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE), "Upload resource", false,
            true),
    MODIFY_A_CUSTOM_RESOURCE(setKey(EnrichmentMailing.MODIFY_CUSTOM_RESOURCE), "Modify a custom resource", false,
        true),
    DELETE_A_CUSTOM_RESOURCE(setKey(EnrichmentMailing.DELETE_CUSTOM_RESOURCE), "Delete a custom resource", false,
        true),
    MODIFY_A_DEFAULT_RESOURCE(setKey(EnrichmentMailing.MODIFY_DEFAULT_RESOURCE), "Modify a default resource", false,
        true),
    DELETE_A_DEFAULT_RESOURCE(setKey(EnrichmentMailing.DELETE_DEFAULT_RESOURCE), "Delete a default resource", false,
        true);

    static {
        PrivilegeKeyValidator.registerKeyExistingValidator(
                EnrichmentMailingPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
                key -> EnumUtils.keyExists(EnrichmentMailingPrivilege.class, key));

        PrivilegeKeyValidator.registerLevelValidator(
                EnrichmentMailingPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
                key -> EnumUtils.getPrivilegeLevel(EnrichmentMailingPrivilege.class, key));
    }


    private final String key;
    private final String value;
    private final boolean isVisibility;
    private final boolean isModification;


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
        return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_ENRICHMENT_MAILING.concat(
                "_".concat(subKey));
    }
}
