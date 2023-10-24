package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.ReadProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingProcessExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalProcessingService {

  private final ApprovingProcessExecutionManager approvingProcessExecutionManager;

  private final ReadProcessExecutionManager readProcessExecutionManager;

  private final ImpersonateTokenService impersonateTokenService;

  public void approve(String flowId, String uuid) {
    var cxt = ProcessControlUtils.getProject(flowId, uuid);
    cxt.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.approvingProcessExecutionManager.execute(cxt);
  }

  public void read(String flowId, String uuid) {
    var cxt = ProcessControlUtils.getProject(flowId, uuid);
    cxt.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.readProcessExecutionManager.execute(cxt);
  }

  public void approveExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    this.approve(param.getFlowId(), param.getUuid());
  }

  public void readExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    this.read(param.getFlowId(), param.getUuid());
  }

  private TokenParam getParams(String companyUuid, String token) {
    return this.impersonateTokenService.validateImpersonateToken(companyUuid, token);
  }
}
