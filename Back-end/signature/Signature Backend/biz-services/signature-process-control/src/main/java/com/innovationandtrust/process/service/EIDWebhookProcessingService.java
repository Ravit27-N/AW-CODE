package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.eid.EIDVideoVerifiedProcessExecutionManager;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.eid.model.VideoVerifiedDto;
import org.springframework.stereotype.Service;

@Service
public class EIDWebhookProcessingService {

  private final EIDVideoVerifiedProcessExecutionManager videoApprovedProcessExecutionManager;

  public EIDWebhookProcessingService(
      EIDVideoVerifiedProcessExecutionManager videoApprovedProcessExecutionManager) {
    this.videoApprovedProcessExecutionManager = videoApprovedProcessExecutionManager;
  }

  /**
   * Handling of the process video verification from EID registry.
   *
   * @param videoVerifiedDto {@link VideoVerifiedDto}.
   */
  public void videoVerificationCallback(VideoVerifiedDto videoVerifiedDto) {
    ExecutionContext ctx = new ExecutionContext();
    ctx.put(SignProcessConstant.VIDEO_VERIFIED, videoVerifiedDto);
    this.videoApprovedProcessExecutionManager.execute(ctx);
  }
}
