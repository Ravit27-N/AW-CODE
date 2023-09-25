package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserCreationHandler extends AbstractExecutionHandler {

  private final KeycloakService keycloakService;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final UserRecord userRecord = context.get(DataReaderConstant.USER_RECORD, UserRecord.class);
    final String password = context.get(DataReaderConstant.PASSWORD_RANDOMIZED, String.class);
    final String defaultGroup = context.get(DataReaderConstant.KEY_CLOAK_DEFAULT_USE_GROUP,
        String.class);

    final User user = new User("", userRecord.getEmail(), userRecord.getFirstName(),
        userRecord.getLastName(), userRecord.getEmail(), new Date(), password);

    final User keycloakUser = this.keycloakService.createUser(user, password,
        List.of(defaultGroup));

    context.put(DataReaderConstant.KEY_CLOAK_USER, keycloakUser);

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
              log.debug("Rollback create keycloak user");
              keycloakService.deleteUser(keycloakUser.getId());
              context.put(DataReaderConstant.KEY_CLOAK_USER, null);
            }
          }
        });
    return ExecutionState.NEXT;
  }
}
