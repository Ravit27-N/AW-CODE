package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.constant.DataReaderValidationType;
import com.tessi.cxm.pfl.ms5.dto.UserOrganization;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.util.BatchUserOrganization;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ClientAdminUserValidationHandler extends UserValidationHandler {

  protected static final String USER_BAD_REQUEST =
      "The client, service or division is not exist in database";
  protected static final String PROFILE_NOT_EXIST = "The profiles are not exist in database";

  @Override
  protected boolean shouldExecute(ExecutionContext context) {
    String userAccessLevel = context.get(DataReaderConstant.USER_ACCESS_LEVEL, String.class);
    return DataReaderValidationType.CLIENT_ADMIN.getValue().equalsIgnoreCase(userAccessLevel);
  }

  @Override
  protected ExecutionState executeInternal(ExecutionContext context) {
    BatchUserOrganization batchUserOrganization =
        context.get(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, BatchUserOrganization.class);
    UserRecord userRecord = context.get(DataReaderConstant.USER_RECORD, UserRecord.class);

    validateUserUserOrg(batchUserOrganization, userRecord, context);
    validateUserProfiles(context, userRecord);

    return ExecutionState.NEXT;
  }

  private void validateUserUserOrg(
      BatchUserOrganization batchUserOrganization,
      UserRecord userRecord,
      ExecutionContext context) {
    UserOrganization validOrgInfo =
        batchUserOrganization.getValidOrgInfo(
            userRecord.getClient(), userRecord.getDivision(), userRecord.getService());

    // Validate user organization
    if (validOrgInfo == null) {
      throw new BatchUserCreationFailureException(USER_BAD_REQUEST);
    }

    Department validDepartment =
        Department.builder()
            .id(validOrgInfo.getServiceId())
            .name(validOrgInfo.getServiceName())
            .build();

    context.put(DataReaderConstant.DEPARTMENT_ENTITY, validDepartment);
  }

  @SuppressWarnings("unchecked")
  private void validateUserProfiles(ExecutionContext context, UserRecord userRecord) {
    List<Profile> profiles =
        context.get(DataReaderConstant.KEY_USER_ORGANIZATION_PROFILES, List.class);

    List<Profile> matchingProfiles =
        profiles.stream()
            .filter(profile -> containsIgnoreCase(userRecord.getProfiles(), profile.getName()))
            .collect(Collectors.toList());

    if (userRecord.getProfiles().size() != matchingProfiles.size()) {
      throw new BatchUserCreationFailureException(PROFILE_NOT_EXIST);
    }

    context.put(DataReaderConstant.PROFILES_ENTITY, matchingProfiles);
  }

  private boolean containsIgnoreCase(List<String> list, String value) {
    return list.stream().anyMatch(item -> item.equalsIgnoreCase(value));
  }
}
