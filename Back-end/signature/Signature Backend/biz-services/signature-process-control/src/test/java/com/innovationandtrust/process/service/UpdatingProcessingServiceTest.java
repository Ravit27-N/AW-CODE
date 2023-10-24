package com.innovationandtrust.process.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.CancelProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.refuse.RefusingProcessExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UpdatingProcessingServiceTest {

  private UpdatingProcessingService updatingProcessingService;
  @Mock private RefusingProcessExecutionManager refusingProcessExecutionManager;
  @Mock private CancelProcessExecutionManager cancelProcessExecutionManager;
  @Mock private ImpersonateTokenService impersonateTokenService;
  private ExecutionContext context;
  private TokenParam param;
  private String flowId;
  private String uuid;
  private static final String COMMENT = "Refuse comment";

  @BeforeEach
  public void setup() {
    updatingProcessingService =
        spy(
            new UpdatingProcessingService(
                refusingProcessExecutionManager,
                cancelProcessExecutionManager,
                impersonateTokenService));

    flowId = "022e2923-924b-4745-a2a8-250077141b83";
    uuid = "faf46aef-f9a5-4222-bfc4-a52fbc2991d1";
    String companyUuid = "244eb546-2343-41d4-8c47-9c5d1ec947e0";
    String token =
        "DUCBtmFRpgN0Ya8McWP7K0C6jSqxC9UITjL-vS-pW_qCNb3vYqBI0hngHZGs_nxgygRrDNQZJn_hlNm772p73_aAjjAuEPXzfxivUJktqEtCZGVzieZyvMGgWJHR18XrIZkPNLyZlyV_4tQmiN1fJstgW5qxVeR6xVWhkpCXe6c";

    param =
        TokenParam.builder()
            .companyUuid(companyUuid)
            .flowId(flowId)
            .uuid(uuid)
            .token(token)
            .build();

    context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @DisplayName("Refuse project test")
  void refuse_project_test() {
    // when
    this.refusingProcessExecutionManager.execute(context);
    this.updatingProcessingService.refuse(flowId, uuid, COMMENT);

    verify(updatingProcessingService, times(1)).refuse(flowId, uuid, COMMENT);
  }

  @Test
  @DisplayName("[Public] Recipient retrieved project test")
  void retrieved_external_project_test() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.updatingProcessingService.refuseExternal(
        param.getCompanyUuid(), COMMENT, param.getToken());
    verify(updatingProcessingService, times(1))
        .refuseExternal(param.getCompanyUuid(), COMMENT, param.getToken());
  }

  @Test
  @DisplayName("Cancel project test")
  void cancel_project_test() {
    // when
    this.cancelProcessExecutionManager.execute(context);
    this.updatingProcessingService.cancel(flowId);

    verify(updatingProcessingService, times(1)).cancel(flowId);
  }
}
