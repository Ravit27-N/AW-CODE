package com.innovationandtrust.process.chain.handler.approve;

import com.innovationandtrust.process.chain.execution.approve.ApprovingCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingIndividualSignExecutionManager;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApprovingDecisionHandler extends AbstractExecutionHandler {

  private final ApprovingCounterSignExecutionManager approvingCounterSignExecutionManager;

  private final ApprovingCoSignExecutionManager approvingCoSignExecutionManager;

  private final ApprovingIndividualSignExecutionManager approvingIndividualSignExecutionManager;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = ProcessControlUtils.getProject(context);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    switch (project.getTemplate().getSignProcess()) {
      case COUNTER_SIGN -> approvingCounterSignExecutionManager.execute(context);
      case COSIGN -> approvingCoSignExecutionManager.execute(context);
      default -> approvingIndividualSignExecutionManager.execute(context);
    }
    return ExecutionState.NEXT;
  }
}
