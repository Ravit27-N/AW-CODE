package com.innovationandtrust.process.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.CompleteSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectIndividualSignExecutionManager;
import com.innovationandtrust.process.chain.execution.expired.UpdateProjectExecutionManager;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ValidateCorporateSettingHandler;
import com.innovationandtrust.process.chain.handler.eid.IdentityDocumentHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.date.DateUtil;
import com.innovationandtrust.utils.exception.exceptions.InvalidTTLValueException;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
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
  private JsonFileProcessHandler jsonFileProcessHandler;
  @Mock private ProjectCoSignExecutionManager coSignExecutionManager;
  @Mock private ProjectCounterSignExecutionManager counterSignExecutionManager;
  @Mock private ProjectIndividualSignExecutionManager individualSignExecutionManager;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private IdentityDocumentHandler identityDocumentHandler;
  @Mock private ProjectWebHookHandler projectWebHookHandler;
  @Mock private SignedDocumentService signedDocumentService;
  @Mock private UpdateProjectExecutionManager updateProjectExecutionManager;
  private CompleteSignProcessExecutionManager completeSignProcessExecutionManager;
  @Mock private ApiNGProperty apiNgProperty;

  private ExecutionContext context;
  private CorporateInfo corporateInfo;
  private Project project;
  private final String flowId = UnitTestConstant.FLOW_ID;
  @Mock ValidateCorporateSettingHandler validateCorporateSettingHandler;

  @BeforeAll
  public static void init() {
    try (var ignored = mockStatic(CompanySettingUtils.class)) {}
  }

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    completeSignProcessExecutionManager =
        spy(
            new CompleteSignProcessExecutionManager(
                jsonFileProcessHandler,
                spy(
                    new DocumentProcessingHandler(signedDocumentService)),
                projectWebHookHandler));
    completeSignProcessExecutionManager.afterPropertiesSet();

    requestSignService =
        spy(
            new RequestSignService(
                coSignExecutionManager,
                counterSignExecutionManager,
                individualSignExecutionManager,
                corporateProfileFeignClient,
                updateProjectExecutionManager,
                apiNgProperty,
                completeSignProcessExecutionManager,
                identityDocumentHandler,
                mock(SettingProperties.class)));

    String companyUuid = UnitTestConstant.COMPANY_UUID;
    corporateInfo =
        CorporateInfo.builder().companyUuid(companyUuid).companyName("Certigna").build();

    context = UnitTestProvider.getContext();
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    project = spy(ProcessControlUtils.getProject(context));
    project.getDetail().setExpireDate(DateUtil.plushDays(new Date(), 2));
  }

  @Test
  @DisplayName("Request sign project test")
  void testRequestSignTest() {
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
    project.getDetail().setExpireDate(new Date());
    Exception exception =
        assertThrows(
            InvalidTTLValueException.class,
            () -> this.requestSignService.requestSign(project),
            UnitTestConstant.ASSERT_EXCEPTION);
    verify(requestSignService, times(6)).requestSign(project);
    String expectedMessage = "You must select a date in the future";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage), UnitTestConstant.ASSERT_TRUE);
  }

  @Test
  @DisplayName("Update project expiration date test")
  void testUpdateExpiredDateProjectTest() {
    // when
    String newExpired = "2023-09-22T00:00:00Z";
    this.updateProjectExecutionManager.execute(context);
    this.requestSignService.updateExpireDate(flowId, newExpired);
    verify(requestSignService, times(1)).updateExpireDate(flowId, newExpired);
  }

  @Test
  @DisplayName("Download manifest file test")
  void testDownloadManifestFileTest() {
    // given
    final FileResponse fileResponse =
        new FileResponse(
            "Manifest.pdf".getBytes(), 1024L, MediaType.APPLICATION_PDF_VALUE, "Manifest.pdf");
    project.getParticipants().forEach(UnitTestProvider::toProcessed);
    var userCompany = new UserCompany();
    userCompany.setUserEntityId(UUID.fromString(UnitTestConstant.UUID));
    userCompany.setCreatedBy(1L);
    var user = UnitTestProvider.getUser();

    // update project to be finished for possible downloading manifest.
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    jsonFileProcessHandler.execute(context);

    when(profileFeignClient.findCompanyUserById(anyLong())).thenReturn(userCompany);
    when(profileFeignClient.findUserById(anyLong())).thenReturn(user);
    when(apiNgFeignClient.downloadManifest(anyLong())).thenReturn("byte[]".getBytes());

    this.completeSignProcessExecutionManager.execute(context);
    this.requestSignService.downloadManifest(flowId);
    verify(requestSignService, times(1)).downloadManifest(flowId);

    when(this.requestSignService.downloadManifest(flowId)).thenReturn(fileResponse);
    assertNotNull(fileResponse, UnitTestConstant.ASSERT_NOT_NULL);

    // update json file to original project for other test case
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
  }

  @Test
  @DisplayName("Check is finished project test")
  void testIsFinishedProjectTest() {
    this.completeSignProcessExecutionManager.execute(context);
    this.requestSignService.isFinished(flowId);
    verify(requestSignService, times(1)).isFinished(flowId);
  }
}
