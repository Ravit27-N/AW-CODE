package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.CancelProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.refuse.RefusingProcessExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UpdatingProcessingService {

  private final RefusingProcessExecutionManager refusingProcessExecutionManager;
  private final CancelProcessExecutionManager cancelProcessExecutionManager;
  private final ImpersonateTokenService impersonateTokenService;

  public UpdatingProcessingService(
      RefusingProcessExecutionManager refusingProcessExecutionManager,
      CancelProcessExecutionManager cancelProcessExecutionManager,
      ImpersonateTokenService impersonateTokenService) {
    this.refusingProcessExecutionManager = refusingProcessExecutionManager;
    this.cancelProcessExecutionManager = cancelProcessExecutionManager;
    this.impersonateTokenService = impersonateTokenService;
  }

  public void refuse(String flowId, String uuid, String comment) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    context.put(SignProcessConstant.COMMENT, comment);
    this.refusingProcessExecutionManager.execute(context);
  }

  public void refuseExternal(String companyUuid, String comment, String token) {
    var param = this.impersonateTokenService.validateImpersonateToken(companyUuid, token);
    this.refuse(param.getFlowId(), param.getUuid(), comment);
  }

  public void cancel(String flowId) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.cancelProcessExecutionManager.execute(context);
  }

  public void updateExpired(List<String> flowIds) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.STATUS, ProjectStatus.EXPIRED.name());
    context.put(SignProcessConstant.FLOW_IDS, flowIds);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ_MULTIPLE);
    this.cancelProcessExecutionManager.execute(context);
  }
}
