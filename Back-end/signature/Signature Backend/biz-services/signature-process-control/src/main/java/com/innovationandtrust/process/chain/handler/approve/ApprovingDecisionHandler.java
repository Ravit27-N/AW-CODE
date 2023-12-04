package com.innovationandtrust.process.chain.handler.approve;

import com.innovationandtrust.process.chain.execution.approve.ApprovingCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingIndividualSignExecutionManager;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import org.springframework.stereotype.Component;

@Component
public class ApprovingDecisionHandler extends AbstractExecutionHandler {

  private final ApprovingCounterSignExecutionManager approvingCounterSignExecutionManager;

  private final ApprovingCoSignExecutionManager approvingCoSignExecutionManager;

  private final ApprovingIndividualSignExecutionManager approvingIndividualSignExecutionManager;

  public ApprovingDecisionHandler(
      ApprovingCounterSignExecutionManager approvingCounterSignExecutionManager,
      ApprovingCoSignExecutionManager approvingCoSignExecutionManager,
      ApprovingIndividualSignExecutionManager approvingIndividualSignExecutionManager) {
    this.approvingCounterSignExecutionManager = approvingCounterSignExecutionManager;
    this.approvingCoSignExecutionManager = approvingCoSignExecutionManager;
    this.approvingIndividualSignExecutionManager = approvingIndividualSignExecutionManager;
  }

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
