package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;

public abstract class UserValidationHandler extends AbstractExecutionHandler {

  protected abstract boolean shouldExecute(ExecutionContext context);

  protected abstract ExecutionState executeInternal(ExecutionContext context);

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    if (shouldExecute(context)) {
      return executeInternal(context);
    }

    return ExecutionState.NEXT;
  }
}
