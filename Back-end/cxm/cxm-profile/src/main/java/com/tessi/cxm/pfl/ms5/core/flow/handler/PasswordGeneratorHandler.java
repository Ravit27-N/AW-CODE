package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.util.PasswordUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import org.springframework.stereotype.Component;

@Component
public class PasswordGeneratorHandler extends AbstractExecutionHandler {

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    String randomPassword = PasswordUtils.generateStrongPassword();
    context.put(DataReaderConstant.PASSWORD_RANDOMIZED, randomPassword);
    return ExecutionState.NEXT;
  }
}
