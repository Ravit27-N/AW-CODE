package com.innovationandtrust.process.chain.handler.sign;

import com.innovationandtrust.process.chain.execution.sign.CoSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.CounterSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.IndividualSignProcessExecutionManager;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigningProcessDecisionHandler extends AbstractExecutionHandler {

  private final CounterSignProcessExecutionManager counterSignProcessExecutionManager;
  private final CoSignProcessExecutionManager coSignProcessExecutionManager;
  private final IndividualSignProcessExecutionManager individualSignProcessExecutionManager;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    switch (project.getTemplate().getSignProcess()) {
      case COUNTER_SIGN -> counterSignProcessExecutionManager.execute(context);
      case COSIGN -> coSignProcessExecutionManager.execute(context);
      default -> individualSignProcessExecutionManager.execute(context);
    }
    return ExecutionState.NEXT;
  }
}
