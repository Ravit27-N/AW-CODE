package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EIDValidateOTPSignHandler extends AbstractExecutionHandler {

  @Override
  public ExecutionState execute(ExecutionContext context) {

    log.info("[EIDValidateOTPSignHandler] Validate sign success or failure.");
    final boolean signDocument = context.get(SignProcessConstant.SIGN_DOCUMENT, Boolean.class);
    if (signDocument) {
      return ExecutionState.NEXT;
    }
    log.error("[EIDValidateOTPSignHandler] EID signing failed.");
    return ExecutionState.END;
  }
}
