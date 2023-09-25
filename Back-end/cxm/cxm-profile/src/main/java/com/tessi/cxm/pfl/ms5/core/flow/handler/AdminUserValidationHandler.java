package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.constant.DataReaderValidationType;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserValidationHandler extends UserValidationHandler {
  protected static final String USER_RECORD_BAD_REQUEST = "The user record must not be null";
  protected static final String USER_BAD_REQUEST = "The client, service or division is not exist in database";
  protected static final String PROFILE_NOT_EXIST = "The profiles are not exist in database";
  private final ClientRepository clientRepository;
  private final ProfileRepository profileRepository;

  @Override
  protected boolean shouldExecute(ExecutionContext context) {
    String userAccessLevel = context.get(DataReaderConstant.USER_ACCESS_LEVEL, String.class);
    return DataReaderValidationType.ADMIN.getValue().equalsIgnoreCase(userAccessLevel);
  }

  @Override
  protected ExecutionState executeInternal(ExecutionContext context) {
    final UserRecord userRecord = context.get(DataReaderConstant.USER_RECORD, UserRecord.class);
    if (userRecord == null) {
      throw new BatchUserCreationFailureException(USER_RECORD_BAD_REQUEST);
    }

    final Department department = clientRepository.findDepartment(userRecord.getClient(),
            userRecord.getDivision(), userRecord.getService())
        .orElseThrow(() -> new BatchUserCreationFailureException(USER_BAD_REQUEST));
    context.put(DataReaderConstant.DEPARTMENT_ENTITY, department);

    var reqProfiles =
        userRecord.getProfiles().stream()
            .map(String::toLowerCase)
            .distinct()
            .collect(Collectors.toList());
    final List<Profile> profiles =
        profileRepository.findProfiles(reqProfiles, department.getDivision().getClient().getId());
    if (profiles.size() != reqProfiles.size()) {
      throw new BatchUserCreationFailureException(PROFILE_NOT_EXIST);
    }
    context.put(DataReaderConstant.PROFILES_ENTITY, profiles);

    return ExecutionState.NEXT;
  }
}
