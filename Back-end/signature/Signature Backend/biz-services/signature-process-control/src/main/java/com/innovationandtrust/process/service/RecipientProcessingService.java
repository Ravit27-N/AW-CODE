package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.RecipientExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import org.springframework.stereotype.Service;

@Service
public class RecipientProcessingService {

  private final RecipientExecutionManager recipientExecutionManager;
  private final ImpersonateTokenService impersonateTokenService;

  public RecipientProcessingService(
      RecipientExecutionManager recipientExecutionManager,
      ImpersonateTokenService impersonateTokenService) {
    this.recipientExecutionManager = recipientExecutionManager;
    this.impersonateTokenService = impersonateTokenService;
  }

  public void recipient(String flowId, String uuid) {
    var cxt = ProcessControlUtils.getProject(flowId, uuid);
    cxt.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.recipientExecutionManager.execute(cxt);
  }

  public void recipientExternal(String companyUuid, String token) {
    var param = this.impersonateTokenService.validateImpersonateToken(companyUuid, token);
    this.recipient(param.getFlowId(), param.getUuid());
  }
}
