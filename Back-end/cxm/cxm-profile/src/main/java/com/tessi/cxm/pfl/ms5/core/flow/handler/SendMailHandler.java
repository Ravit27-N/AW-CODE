package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.dto.CreateUserRequestDTO;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.service.UserService;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.utils.SerialExecutor;
import java.util.concurrent.Executors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SendMailHandler extends AbstractExecutionHandler {

  private final UserService userService;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    UserEntity userEntity = context.get(DataReaderConstant.USER_ENTITY, UserEntity.class);
    if (userEntity == null) {
      throw new BatchUserCreationFailureException("The user entity must be not null");
    }

    String randomPassword = context.get(DataReaderConstant.PASSWORD_RANDOMIZED, String.class);
    if (randomPassword == null) {
      throw new BatchUserCreationFailureException("The password randomized must be not null");
    }

    String superAdminUserName = context.get(DataReaderConstant.SUPER_ADMIN_USER_NAME, String.class);
    if (superAdminUserName == null) {
      throw new BatchUserCreationFailureException("The super admin username must be not null");
    }

    String superAdminId = context.get(DataReaderConstant.SUPER_ADMIN_ID, String.class);
    if (superAdminId == null) {
      throw new BatchUserCreationFailureException("The super adminID must not be null");
    }

    CreateUserRequestDTO userRequestDTO = CreateUserRequestDTO.builder()
        .firstName(userEntity.getFirstName())
        .lastName(userEntity.getLastName())
        .email(userEntity.getEmail())
        .password(randomPassword)
        .build();

    log.info("Start calling Send notification mail of creation new user account {} - t : {} ",
        userRequestDTO.getEmail(), superAdminUserName);

    var executor = new SerialExecutor(Executors.newSingleThreadExecutor());
    executor.execute(
        () -> {
          try {
            this.userService.sendConfirmationOfCreatedAccountByEmailForm(
                userRequestDTO, superAdminUserName, superAdminId);
          } catch (Exception ex) {
            log.error("Failed to send emails to the created user", ex);
          }
        });

    log.info("End calling Send notification mail of creation new user account {} ",
        userRequestDTO.getEmail());

    return ExecutionState.NEXT;
  }
}
