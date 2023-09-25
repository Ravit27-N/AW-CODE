package com.tessi.cxm.pfl.ms5.core.flow;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.core.flow.handler.DBUserCreationHandler;
import com.tessi.cxm.pfl.ms5.core.flow.handler.KeycloakUserCreationHandler;
import com.tessi.cxm.pfl.ms5.core.flow.handler.PasswordGeneratorHandler;
import com.tessi.cxm.pfl.ms5.core.flow.handler.SendMailHandler;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.ms5.core.flow.handler.UserValidationHandler;
import com.tessi.cxm.pfl.ms5.core.flow.handler.ValidationRecordHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserCreationManager extends ExecutionManager implements InitializingBean {

  private final ValidationRecordHandler validationRecordHandler;
  private final List<UserValidationHandler> userValidationHandlers;
  private final PasswordGeneratorHandler passwordGeneratorHandler;
  private final KeycloakUserCreationHandler keycloakUserCreatorHandler;
  private final DBUserCreationHandler dbUserCreationHandler;
  private final SendMailHandler sendMailHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandler(validationRecordHandler);
    userValidationHandlers.forEach(this::addHandler);
    this.addHandler(passwordGeneratorHandler);
    this.addHandler(keycloakUserCreatorHandler);
    this.addHandler(dbUserCreationHandler);
    this.addHandler(sendMailHandler);
  }

  public void create(UserRecord userRecord, ExecutionContext context) {
    context.put(DataReaderConstant.USER_RECORD, userRecord);
    super.execute(context);
  }
}
