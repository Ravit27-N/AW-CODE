package com.innovationandtrust.process.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.CompleteSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectIndividualSignExecutionManager;
import com.innovationandtrust.process.chain.execution.expired.UpdateProjectExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.utils.DateUtil;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.enums.SignatureFormat;
import com.innovationandtrust.share.enums.SignatureLevel;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectDetail;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.InvalidTTLValueException;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RequestSignProcessingServiceTest {
  private RequestSignService requestSignService;
  @Mock private ProjectCoSignExecutionManager coSignExecutionManager;
  @Mock private ProjectCounterSignExecutionManager counterSignExecutionManager;
  @Mock private ProjectIndividualSignExecutionManager individualSignExecutionManager;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private UpdateProjectExecutionManager updateProjectExecutionManager;
  @Mock private CompleteSignProcessExecutionManager completeSignProcessExecutionManager;
  @Mock private ApiNGProperty apiNgProperty;
  private ExecutionContext context;
  private CorporateInfo corporateInfo;
  private Project project;
  private String flowId;

  @BeforeEach
  public void setup() {
    requestSignService =
        spy(
            new RequestSignService(
                coSignExecutionManager,
                counterSignExecutionManager,
                individualSignExecutionManager,
                corporateProfileFeignClient,
                updateProjectExecutionManager,
                apiNgProperty,
                completeSignProcessExecutionManager));

    flowId = "022e2923-924b-4745-a2a8-250077141b83";
    String uuid = "faf46aef-f9a5-4222-bfc4-a52fbc2991d1";
    String companyUuid = "244eb546-2343-41d4-8c47-9c5d1ec947e0";

    corporateInfo =
        CorporateInfo.builder().companyUuid(companyUuid).companyName("Certigna").build();

    context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);

    Template template =
        Template.builder()
            .name("Template")
            .signProcess(ScenarioStep.COUNTER_SIGN)
            .approvalProcess(ScenarioStep.COUNTER_SIGN)
            .level(SignatureLevel.LT)
            .format(SignatureFormat.CA_DES)
            .build();

    project = ProcessControlUtils.getProject(context);
    project.setFlowId(flowId);
    project.setCorporateInfo(corporateInfo);
    project.setTemplate(template);
    project.setDetail(
        new ProjectDetail(Collections.emptyList(), DateUtil.plushDays(new Date(), 2), 1L, 1L));
  }

  @Test
  @DisplayName("Request sign project test")
  void request_sign_test() {
    // when
    when(this.corporateProfileFeignClient.findCorporateInfo(any())).thenReturn(corporateInfo);
    ProcessControlUtils.checkCompanyInfo(corporateInfo, flowId);

    this.counterSignExecutionManager.execute(context);
    this.requestSignService.requestSign(project);
    verify(requestSignService, times(1)).requestSign(project);

    project.getTemplate().setSignProcess(ScenarioStep.COSIGN);
    this.coSignExecutionManager.execute(context);
    this.requestSignService.requestSign(project);
    verify(requestSignService, times(2)).requestSign(project);

    project.getTemplate().setSignProcess(ScenarioStep.INDIVIDUAL_SIGN);
    this.individualSignExecutionManager.execute(context);
    this.requestSignService.requestSign(project);
    verify(requestSignService, times(3)).requestSign(project);

    project.setCorporateInfo(null);
    this.requestSignService.requestSign(project);
    verify(requestSignService, times(4)).requestSign(project);

    project.setFlowId(null);
    this.requestSignService.requestSign(project);
    verify(requestSignService, times(5)).requestSign(project);

    Exception exception =
        assertThrows(
            InvalidTTLValueException.class,
            () -> {
              project.getDetail().setExpireDate(new Date());
              this.requestSignService.requestSign(project);
              verify(requestSignService, times(6)).requestSign(project);
            });

    String expectedMessage = "You must select a date in the future";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @DisplayName("Update project expiration date test")
  void update_expired_date_project_test() {
    // when
    String newExpired = "2023-09-22T00:00:00Z";
    this.updateProjectExecutionManager.execute(context);
    this.requestSignService.updateExpireDate(flowId, newExpired);
    verify(requestSignService, times(1)).updateExpireDate(flowId, newExpired);
  }

  @Test
  @DisplayName("Download manifest file test")
  void download_manifest_file_test() {
    final FileResponse fileResponse = new FileResponse("Manifest.pdf".getBytes(), 1024L, MediaType.APPLICATION_PDF_VALUE, "Manifest.pdf");
    this.completeSignProcessExecutionManager.execute(context);
    this.requestSignService.downloadManifest(flowId);
    verify(requestSignService, times(1)).downloadManifest(flowId);

    when(this.requestSignService.downloadManifest(flowId)).thenReturn(fileResponse);
    assertNotNull(fileResponse);
  }

  @Test
  @DisplayName("Check is finished project test")
  void is_finished_project_test() {
    this.completeSignProcessExecutionManager.execute(context);
    this.requestSignService.isFinished(flowId);
    verify(requestSignService, times(1)).isFinished(flowId);
  }
}
