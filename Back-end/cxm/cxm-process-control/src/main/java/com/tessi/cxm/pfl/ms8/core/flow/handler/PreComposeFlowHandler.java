package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PreComposeFlowHandler extends AbstractExecutionHandler {

  @Override
  protected ExecutionState execute(ExecutionContext context) {

    return ExecutionState.NEXT;
  }
}
