package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.chain.execution.eid.EIDCoSignDocumentExecuteManager;
import com.innovationandtrust.process.chain.execution.eid.EIDCounterSignDocumentExecuteManager;
import com.innovationandtrust.process.constant.DocumentProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EIDSigningProcessDecisionHandler extends AbstractExecutionHandler {

  private final EIDCoSignDocumentExecuteManager eIDCoSignDocumentExecuteManager;
  private final EIDCounterSignDocumentExecuteManager eIDCounterSignDocumentExecuteManager;

  public EIDSigningProcessDecisionHandler(
      EIDCoSignDocumentExecuteManager eIDCoSignDocumentExecuteManager,
      EIDCounterSignDocumentExecuteManager eIDCounterSignDocumentExecuteManager) {
    this.eIDCoSignDocumentExecuteManager = eIDCoSignDocumentExecuteManager;
    this.eIDCounterSignDocumentExecuteManager = eIDCounterSignDocumentExecuteManager;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    switch (project.getTemplate().getSignProcess()) {
      case COUNTER_SIGN -> eIDCounterSignDocumentExecuteManager.execute(context);
      case COSIGN -> eIDCoSignDocumentExecuteManager.execute(context);
      default -> {}
    }
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.STORE_SIGNED_DOCUMENTS);
    return ExecutionState.NEXT;
  }
}
