package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles;
import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.model.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class DBUserCreationHandler extends AbstractExecutionHandler {

  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;

  @SuppressWarnings("unchecked")
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final User keycloakUser = context.get(DataReaderConstant.KEY_CLOAK_USER, User.class);
    final Department department =
        context.get(DataReaderConstant.DEPARTMENT_ENTITY, Department.class);

    if (department == null) {
      throw new BatchUserCreationFailureException("The department must be not null");
    }

    var userEntity =
        UserEntity.builder()
            .technicalRef(keycloakUser.getId())
            .username(keycloakUser.getUsername())
            .firstName(keycloakUser.getFirstName())
            .lastName(keycloakUser.getLastName())
            .email(keycloakUser.getUsername())
            .department(department)
            .isActive(true)
            .isAdmin(false)
            .build();

    // Create user entity.
    final UserEntity userEntityResponse = this.userRepository.saveAndFlush(userEntity);

    // Assign profiles to user.
    final List<Profile> profiles =
        (List<Profile>) context.get(DataReaderConstant.PROFILES_ENTITY, List.class);

    if (CollectionUtils.isEmpty(profiles)) {
      throw new BatchUserCreationFailureException("The profiles must not be null");
    }

    var userProfiles =
        profiles.stream()
            .map(profile -> new UserProfiles(userEntityResponse, profile))
            .collect(Collectors.toList());

    final List<UserProfiles> userProfileResponses = this.userProfileRepository.saveAllAndFlush(
        userProfiles);

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
              userProfileRepository.deleteAll(userProfileResponses);
              userRepository.delete(userEntityResponse);
            }
          }
        });

    context.put(DataReaderConstant.USER_ENTITY, userEntityResponse);
    return ExecutionState.NEXT;
  }
}
