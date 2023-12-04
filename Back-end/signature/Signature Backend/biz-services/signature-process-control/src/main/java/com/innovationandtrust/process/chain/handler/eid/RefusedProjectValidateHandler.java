package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefusedProjectValidateHandler extends AbstractExecutionHandler {

  @Override
  public ExecutionState execute(ExecutionContext context) {

    log.info("[RefusedProjectValidateHandler] Processing validate to refused project.");
    final String verifiedStatus =
        context.get(SignProcessConstant.VIDEO_VERIFIED_STATUS, String.class);
    if (!ProcessStatus.EID_REJECTED.equalsIgnoreCase(verifiedStatus)) {
      log.warn("[RefusedProjectValidateHandler] Video verified accepted.");
      return ExecutionState.END;
    }

    return ExecutionState.NEXT;
  }
}
