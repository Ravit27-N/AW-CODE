package com.tessi.cxm.pfl.ms8.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.core.flow.BatchFlowProcessingManager;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowAnalyzer;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowDocumentScheduler;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowIdentifier;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowProcessor;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowScheduler;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowSwitcher;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualProcessFlowDocumentValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualProcessFlowValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualReIdentifierFlow;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileMissingException;
import com.tessi.cxm.pfl.ms8.exception.ConfigSignatureAttributeNotFoundException;
import com.tessi.cxm.pfl.ms8.model.DepositStepResponse;
import com.tessi.cxm.pfl.ms8.model.FlowAnalyzeRequest;
import com.tessi.cxm.pfl.ms8.model.FlowTreatmentFlowRequestWrapper;
import com.tessi.cxm.pfl.shared.model.ProcessingResponse;
import com.tessi.cxm.pfl.shared.model.ResponseDocumentProcessingPortal;
import com.tessi.cxm.pfl.ms8.model.SwitchFlowResponse;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.exception.ClientUnloadingNotConfiguredException;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.PreProcessingUpdateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateDepositFlowStep;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateDepositFlowStep.UpdateDepositFlowStepBuilder;
import com.tessi.cxm.pfl.shared.scheduler.SchedulerHandler;
import com.tessi.cxm.pfl.shared.scheduler.model.JobTriggerDescriptor;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.AttachmentPosition;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.ClientSettingCriteriaDistributionValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.CriteriaDistributionChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.FlowDepositArea;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_ESPACE_VALIDATION;

/**
 * The service of process control to provide the ability to launch batch process.
 *
 * @author Sokhour LACH
 * @author Sakal TUM
 * @author Vichet CHANN
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessControlService {

  private static final String CURRENT_SERVER_NAME;
  private static final String ERROR_MESSAGE = "Flow processing encounter an error.";
  private final ResourceFileService resourceFileService;

  static {
    CURRENT_SERVER_NAME = ComputerSystemProduct.getDeviceId();
  }

  private final StreamBridge streamBridge;
  private final KeycloakService keycloakService;
  private final BatchFlowProcessingManager batchFlowProcessingManager;
  private final ManualFlowIdentifier manualFlowIdentifier;
  private final ManualFlowAnalyzer manualFlowAnalyzer;
  private final ManualFlowProcessor manualFlowProcessor;
  private final ManualFlowSwitcher manualFlowSwitcher;
  private final ManualProcessFlowDocumentValidation manualProcessFlowDocumentValidation;
  private final ManualReIdentifierFlow manualReIdentifierFlow;
  ProcessControlSchedulerService processControlSchedulerService;
  private ManualProcessFlowValidation manualProcessFlowValidation;
  private ManualFlowValidation manualFlowValidation;
  private ManualFlowScheduler manualFlowScheduler;
  private ManualFlowDocumentScheduler manualFlowDocumentScheduler;
  private FileCtrlMngtFeignClient fileCtrlMngtFeignClient;
  private ProfileFeignClient profileFeignClient;
  private final SettingFeignClient settingFeignClient;
  private final ResourceFileRepository resourceFileRepository;
  private ModelMapper modelMapper;

  @Autowired
  public void setProfileFeignClient(ProfileFeignClient profileFeignClient) {
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
    this.profileFeignClient = profileFeignClient;
  }

  @Autowired
  public void setManualFlowDocumentScheduler(
      ManualFlowDocumentScheduler manualFlowDocumentScheduler) {
    this.manualFlowDocumentScheduler = manualFlowDocumentScheduler;
  }

  @Autowired
  public void setManualFlowScheduler(ManualFlowScheduler manualFlowScheduler) {
    this.manualFlowScheduler = manualFlowScheduler;
  }

  @Autowired
  public void setManualFlowValidation(ManualFlowValidation manualFlowValidation) {
    this.manualFlowValidation = manualFlowValidation;
  }

  @Autowired
  public void setManualProcessFlowValidation(
      ManualProcessFlowValidation manualProcessFlowValidation) {
    this.manualProcessFlowValidation = manualProcessFlowValidation;
  }

  @Autowired
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * To start flow treatment.
   *
   * @param launchRequest refers object of {@link DepositedFlowLaunchRequest}
   */
  public void launch(DepositedFlowLaunchRequest launchRequest) {
    log.info("Flow treatment launch with : {}", launchRequest);

    var executionContext = this.getExecutionContext(launchRequest);
    executionContext.put(FlowTreatmentConstants.FUNC_KEY, "");
    executionContext.put(FlowTreatmentConstants.PRIV_KEY, "");
    try {
      this.batchFlowProcessingManager.execute(executionContext);
    } catch (RuntimeException exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(executionContext);
    }
  }

  /**
   * To identify the model of a pdf before initialize the flow.
   *
   * @param request refers the object of {@link DepositedFlowLaunchRequest}
   * @param authorizationToken refers to token to use in process
   * @return the object {@link ProcessCtrlIdentificationResponse}
   */
  public ProcessCtrlIdentificationResponse identifyDepositedFlow(
      DepositedFlowLaunchRequest request, String authorizationToken) {
    final long ownerId = PrivilegeValidationUtil.getUserDetail().getOwnerId();
    final Map<String, String> privilege = this.checkUserPrivilege(false, ownerId);
    var processingContext = this.getFlowIdentifyingContext(request, authorizationToken);
    processingContext.putAll(privilege);
    processingContext.put(FlowTreatmentConstants.OWNER_ID, ownerId);

    try {
      if (request.isNew()) {
        this.manualFlowIdentifier.execute(processingContext);
      } else {
        this.manualReIdentifierFlow.execute(processingContext);
      }
    } catch (Exception exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(processingContext);
      return ProcessCtrlIdentificationResponse.builder()
          .modelName("")
          .channel("")
          .subChannel("")
          .build();
    }

    return this.getIdentifiedFlow(processingContext);
  }

  /**
   * To analyze the amount of documents containing in a pdf file flow.
   *
   * @param analyzeRequest refers to the object of {@link FlowAnalyzeRequest}
   * @param authorizationToken refers to the valid token
   * @param isModify refers to the visibility or modification type of the privilege
   * @return the object of {@link PortalDocumentResponse}
   * @see ProcessControlService#checkUserPrivilege(boolean, long)
   * @see ManualFlowAnalyzer#execute(ExecutionContext)
   */
  public PortalDocumentResponse analyzeDepositedFlow(
      FlowAnalyzeRequest analyzeRequest, String authorizationToken, boolean isModify) {
    final Map<String, String> privilege =
        this.checkUserPrivilege(isModify, Long.parseLong(analyzeRequest.getIdCreator()));
    var processingContext = this.getFlowAnalyzingContext(analyzeRequest, authorizationToken);
    processingContext.putAll(privilege);

    try {
      this.manualFlowAnalyzer.execute(processingContext);
    } catch (Exception exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(processingContext);
    }
    final PortalDocumentResponse analyzedFlow = this.getAnalyzedFlow(processingContext);
    if (Boolean.TRUE.equals(
        processingContext.get(FlowTreatmentConstants.DOCUMENT_VALIDATION, Boolean.class))) {
      this.updateFlowDocumentsError(processingContext);
    }
    return analyzedFlow;
  }

  public ResponseDocumentProcessingPortal processingFlow(
      FlowTreatmentFlowRequestWrapper treatmentFlowRequest,
      String authorizationToken,
      boolean isModify) {
    final UserDetail userDetail = PrivilegeValidationUtil.getUserDetail();
    final String username = userDetail.getUsername();
    final long ownerId = Long.parseLong(treatmentFlowRequest.getIdCreator());

    final Map<String, String> privilege = this.checkUserPrivilege(isModify, ownerId);
    this.validateResourceFile(treatmentFlowRequest.getUuid());
    var treatmentContext = this.getFlowTreatmentContext(treatmentFlowRequest, authorizationToken);
    treatmentContext.putAll(privilege);
    DepositedFlowLaunchRequest depositedFlowLaunchRequest =
        treatmentContext.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    depositedFlowLaunchRequest.setFileId(treatmentFlowRequest.getUuid());
    depositedFlowLaunchRequest.setUserName(username);
    treatmentContext.put(
        FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlowLaunchRequest);
    treatmentContext.put(
        FlowTreatmentConstants.FLOW_PROCESSING_STATUS, FlowTraceabilityStatus.TREATMENT.getValue());

    this.validateSignatureConfiguration(
        treatmentContext, treatmentFlowRequest.getUuid(), depositedFlowLaunchRequest.getUuid());

    updateFlowTraceabilityStatus(treatmentContext);
    try {
      this.manualFlowProcessor.execute(treatmentContext);
    } catch (Exception exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(treatmentContext);
    }
    return treatmentContext.get(
        FlowTreatmentConstants.PORTAL_DOCUMENT, ResponseDocumentProcessingPortal.class);
  }

  private void validateSignatureConfiguration(
      ExecutionContext context, String flowId, String uuid) {
    Optional<ResourceFile> resourceFile =
        this.resourceFileRepository.findFirstByFlowIdAndTypeIgnoreCase(
            flowId, ResourceType.SIGNATURE.getValue());
    String token =
        BearerAuthentication.PREFIX_TOKEN.concat(
            context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class));
    var portalJson = this.getDepositStep(uuid, token);
    if (resourceFile.isPresent()
        && !this.settingFeignClient.validateSignatureAttributes(
            portalJson.getFlow().getModelName(), token)) {
      throw new ConfigSignatureAttributeNotFoundException();
    }
  }

  private ExecutionContext getFlowTreatmentContext(
      FlowTreatmentFlowRequestWrapper treatmentFlowRequest, String authorizationToken) {

    var context = new ExecutionContext();

    var flowDeposited =
        DepositedFlowLaunchRequest.builder()
            .uuid(treatmentFlowRequest.getUuid())
            .idCreator(Long.valueOf(treatmentFlowRequest.getIdCreator()))
            .depositType(FlowTreatmentConstants.PORTAL_DEPOSIT)
            .build();

    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, flowDeposited);
    context.put(FlowTreatmentConstants.COMPOSED_FILE_ID, treatmentFlowRequest.getComposedFileId());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, authorizationToken);
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.PRE_PROCESSING);
    context.put(FlowTreatmentConstants.FLOW_PRODUCTION, treatmentFlowRequest.getProduction());
    context.put(FlowTreatmentConstants.IS_TO_VALIDATED, treatmentFlowRequest.isValidation());
    return context;
  }

  public SwitchFlowResponse toValidateFlow(
      String uuid,
      String composedFileId,
      SharedClientUnloadDetailsDTO clientUnloadDetails,
      Map<String, String> privileges) { // add new handler to produce doc to validation
    var context = this.getSendFlowContext(uuid, composedFileId);
    final DepositedFlowLaunchRequest depositedFlowLaunchRequest = new DepositedFlowLaunchRequest();
    depositedFlowLaunchRequest.setDepositType(FlowTreatmentConstants.PORTAL_DEPOSIT);
    depositedFlowLaunchRequest.setUuid(uuid);
    context.putAll(privileges);
    context.put(FlowTreatmentConstants.PORTAL_DEPOSIT, FlowTreatmentConstants.PORTAL_DEPOSIT);
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlowLaunchRequest);
    context.put(FlowTreatmentConstants.IS_VALIDATE_ACTION, false);
    context.put(ProcessControlConstants.CLIENT_UNLOADING_DETAIL, clientUnloadDetails);

    this.manualFlowValidation.execute(context);
    return context.get(FlowTreatmentConstants.SWITCH_FLOW_RESPONSE, SwitchFlowResponse.class);
  }

  public SwitchFlowResponse sendFlow(
      String uuid, String composedFileId, boolean validation, boolean isModify) {
    final String authToken = PrivilegeValidationUtil.getAuthTokenWithPrefix();
    final UserDetail userDetail = PrivilegeValidationUtil.getUserDetail();
    final String username = userDetail.getUsername();
    final PortalFlowFileControl depositStep = getDepositStep(uuid, authToken);
    final long ownerId = Long.parseLong(depositStep.getUserId());
    final Map<String, String> privilege = this.checkUserPrivilege(isModify, ownerId);
    ClientSettingCriteriaDistributionValidationUtil.validateActivatedChannel(
        userDetail.getClientName(), CriteriaDistributionChannel.POSTAL.getValue());
    var clientUnloadDetails = this.profileFeignClient.getClientUnloadDetails(authToken);
    if (clientUnloadDetails.getClientUnloads().isEmpty()) {
      throw new ClientUnloadingNotConfiguredException(clientUnloadDetails.getClientId());
    }
    if (validation) {
      return this.toValidateFlow(uuid, composedFileId, clientUnloadDetails, privilege);
    }
    return this.depositedFlowScheduler(
        uuid, composedFileId, username, clientUnloadDetails, privilege);
  }

  private void validateResourceFile(String flowId) {
    if (this.resourceFileService.checkResourceIsMissing(flowId)) {
      throw new BackgroundFileMissingException("Missing resource");
    }
  }

  @Autowired
  public void setProcessControlSchedulerService(
      ProcessControlSchedulerService processControlSchedulerService) {
    this.processControlSchedulerService = processControlSchedulerService;
  }

  private SwitchFlowResponse depositedFlowScheduler(
      String flowId,
      String composedFileId,
      String username,
      SharedClientUnloadDetailsDTO clientUnloadDetails,
      Map<String, String> privileges) {
    return depositedFlowScheduler(
        flowId, composedFileId, username, clientUnloadDetails, false, privileges);
  }

  private SwitchFlowResponse depositedFlowScheduler(
      String flowId,
      String composedFileId,
      String username,
      SharedClientUnloadDetailsDTO clientUnloadDetails,
      boolean isValidation,
      Map<String, String> privileges) {
    var context = getSendFlowContext(flowId, composedFileId);
    final DepositedFlowLaunchRequest depositedFlowLaunchRequest = new DepositedFlowLaunchRequest();
    depositedFlowLaunchRequest.setDepositType(FlowTreatmentConstants.PORTAL_DEPOSIT);
    depositedFlowLaunchRequest.setUuid(flowId);
    context.putAll(privileges);
    context.put(FlowTreatmentConstants.PORTAL_DEPOSIT, FlowTreatmentConstants.PORTAL_DEPOSIT);
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlowLaunchRequest);
    context.put(FlowTreatmentConstants.CREATED_BY, username);
    context.put(ProcessControlConstants.CLIENT_UNLOADING_DETAIL, clientUnloadDetails);
    context.put(FlowTreatmentConstants.IS_TO_VALIDATED, isValidation);
    try {
      manualFlowScheduler.execute(context);
    } catch (Exception exception) {
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.SWITCH);
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(context);
    }
    return context.get(FlowTreatmentConstants.SWITCH_FLOW_RESPONSE, SwitchFlowResponse.class);
  }

  private ExecutionContext getSendFlowContext(String uuid, String composedFileId) {
    return getSendFlowContext(uuid, composedFileId, false, AuthenticationUtils.getAuthToken());
  }

  private ExecutionContext getSendFlowContext(
      String uuid, String composedFileId, boolean isValidateDocument, String token) {
    var context = new ExecutionContext();
    context.put(FlowTreatmentConstants.FLOW_UUID, uuid);
    context.put(FlowTreatmentConstants.COMPOSED_FILE_ID, composedFileId);
    context.put(FlowTreatmentConstants.BEARER_TOKEN, token);
    context.put(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, isValidateDocument);
    return context;
  }

  /**
   * To prepare the data and request to cxm-switch service for processing the documents of a flow.
   *
   * @param uuid refers to the identity of a flow
   * @param composedFileId refers to identity of file that store as composed file in
   *     cxm-file-manager
   * @param token refers to the valid token for processing the flow across the microservices
   * @param username refers to the identity of a user proceed this process
   * @param processByScheduler refers to the option of switch documents of the flow
   */
  public void switchDepositedFlow(
      String uuid,
      String composedFileId,
      String token,
      String username,
      boolean processByScheduler,
      Date unloadingDate) {
    var context = getSendFlowContext(uuid, composedFileId, false, token);
    context.put(FlowTreatmentConstants.PORTAL_DEPOSIT, FlowTreatmentConstants.PORTAL_DEPOSIT);
    context.put(
        FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
        DepositedFlowLaunchRequest.builder()
            .depositType(FlowTreatmentConstants.PORTAL_DEPOSIT)
            .uuid(uuid)
            .fileId(uuid)
            .userName(username)
            .build());
    context.put(FlowTreatmentConstants.USERNAME, username);
    context.put(FlowTreatmentConstants.IS_VALIDATE_ACTION, false);
    context.put(FlowTreatmentConstants.PROCESS_BY_SCHEDULER, processByScheduler);
    context.put(FlowTreatmentConstants.FORCE_UNLOADING_DATE, unloadingDate);
    try {
      this.manualFlowSwitcher.execute(context);
    } catch (ExecutionException exception) {
      log.error(ERROR_MESSAGE, exception);
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.SWITCH);
      this.reportErrorToKafka(context);
    }
  }

  private ExecutionContext getExecutionContext(DepositedFlowLaunchRequest launchRequest) {
    var context = new ExecutionContext();

    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, launchRequest);
    context.put(FlowTreatmentConstants.FLOW_TYPE, launchRequest.getFlowType());
    try {
      var accessTokenString = this.keycloakService.getToken();
      context.put(FlowTreatmentConstants.BEARER_TOKEN, accessTokenString);
    } catch (RuntimeException exception) {
      log.error("Keycloak service encounter exception.", exception);
    }

    return context;
  }

  private ExecutionContext getFlowIdentifyingContext(
      DepositedFlowLaunchRequest request, String refBearerToken) {
    var context = new ExecutionContext();
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.IDENTIFICATION);
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, request);
    context.put(FlowTreatmentConstants.FLOW_TYPE, request.getFlowType());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, refBearerToken);
    context.put(FlowTreatmentConstants.USERNAME, request.getUserName());
    context.put(FlowTreatmentConstants.USER_FULL_NAME, request.getFullName());
    return context;
  }

  private ExecutionContext getFlowIdentifyingPayload(
      PortalFlowFileControl portalJson, String refBearerToken) {
    var context = new ExecutionContext();
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.IDENTIFICATION);

    var depositedFlow =
        DepositedFlowLaunchRequest.builder()
            .uuid(portalJson.getUuid())
            .depositType(portalJson.getDepositType())
            .fileId(portalJson.getUuid())
            .fileName(portalJson.getFileName())
            .flowType(portalJson.getFlow().getType())
            .idCreator(Long.valueOf(portalJson.getUserId()))
            .build();

    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlow);
    context.put(FlowTreatmentConstants.FLOW_TYPE, portalJson.getFlow().getType());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, refBearerToken);
    context.put(FlowTreatmentConstants.USERNAME, portalJson.getUserName());
    context.put(FlowTreatmentConstants.USER_FULL_NAME, portalJson.getFullName());
    return context;
  }

  private ExecutionContext getFlowAnalyzingContext(
      FlowAnalyzeRequest analyzeRequest, String authorizationToken) {
    var context = new ExecutionContext();

    var flowDeposited =
        DepositedFlowLaunchRequest.builder()
            .uuid(analyzeRequest.getUuid())
            .depositType(FlowTreatmentConstants.PORTAL_DEPOSIT)
            .flowType(analyzeRequest.getFlowType())
            .fileId(analyzeRequest.getFileId())
            .idCreator(Long.valueOf(analyzeRequest.getIdCreator()))
            .serverName(CURRENT_SERVER_NAME)
            .build();

    // In manual process, this step is still in identification.
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.IDENTIFICATION);
    context.put(FlowTreatmentConstants.FLOW_TYPE, analyzeRequest.getFlowType());
    context.put(FlowTreatmentConstants.MODEL_NAME, analyzeRequest.getModelName());
    context.put(FlowTreatmentConstants.CHANNEL, analyzeRequest.getChannel());
    context.put(FlowTreatmentConstants.SUB_CHANNEL, analyzeRequest.getSubChannel());
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, flowDeposited);

    context.put(FlowTreatmentConstants.FLOW_UUID, analyzeRequest.getUuid());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, authorizationToken);

    return context;
  }

  private ProcessCtrlIdentificationResponse getIdentifiedFlow(ExecutionContext context) {
    var status = context.get(FlowTreatmentConstants.FLOW_PROCESSING_STATUS, String.class);
    if (StringUtils.isBlank(status)
        || !FlowTraceabilityStatus.IN_ERROR.getValue().equalsIgnoreCase(status)) {
      return ProcessCtrlIdentificationResponse.builder()
          .modelName(context.get(FlowTreatmentConstants.MODEL_NAME, String.class))
          .channel(context.get(FlowTreatmentConstants.CHANNEL, String.class))
          .subChannel(context.get(FlowTreatmentConstants.SUB_CHANNEL, String.class))
          .build();
    }
    return null;
  }

  private PortalDocumentResponse getAnalyzedFlow(ExecutionContext context) {
    return context.get(FlowTreatmentConstants.PORTAL_DOCUMENT, PortalDocumentResponse.class);
  }

  private void reportErrorToKafka(ExecutionContext executionContext) {
    executionContext.put(
        FlowTreatmentConstants.FLOW_PROCESSING_STATUS, FlowTraceabilityStatus.IN_ERROR.getValue());

    var processControlExecutionStep =
        executionContext.get(
            FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.class);

    if (processControlExecutionStep != null && processControlExecutionStep.getStepOrder() >= 1) {
      this.updateFlowTraceabilityStatus(executionContext);
    }
  }

  /**
   * Produce message to update FLowTraceability's status identified by fileId field.
   *
   * @param context Current execution context. Use for required data to create message.
   */
  private boolean updateFlowTraceabilityStatus(ExecutionContext context) {
    var launchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var message = new BaseUpdateFlowFromProcessCtrl();
    message.setStatus(context.get(FlowTreatmentConstants.FLOW_PROCESSING_STATUS, String.class));
    message.setFileId(launchRequest.getFileId());
    message.setCreatedBy(launchRequest.getUserName());
    message.setServer(CURRENT_SERVER_NAME);

    return this.streamBridge.send(KafkaUtils.UPDATE_FLOW_STATUS_BY_FILE_ID_TOPIC, message);
  }

  /**
   * Produce message to update FLowTraceability's documents status identified by fileId field.
   *
   * @param context Current execution context. Use for required data to create message.
   */
  private void updateFlowDocumentsError(ExecutionContext context) {
    final DepositedFlowLaunchRequest depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var response =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    var preProcessedDocumentDetails =
        context.get(FlowTreatmentConstants.PORTAL_DOCUMENT, PortalDocumentResponse.class);
    var flowModelPreTreatment = new PreProcessingUpdateFlowTraceabilityModel();
    flowModelPreTreatment.setFileId(depositedFlowLaunchRequest.getUuid());
    flowModelPreTreatment.setStatus(FlowTraceabilityStatus.IN_ERROR.getValue());
    flowModelPreTreatment.setServer(depositedFlowLaunchRequest.getServerName());
    flowModelPreTreatment.setCreatedBy(response.getUserName());
    flowModelPreTreatment.setFlowDocuments(
        preProcessedDocumentDetails.getDocument().getFlowDocuments());
    flowModelPreTreatment.setNbDocuments(
        preProcessedDocumentDetails.getDocument().getNbDocuments());
    flowModelPreTreatment.setNbDocumentsKo(
        preProcessedDocumentDetails.getDocument().getNbDocumentsKO());
    flowModelPreTreatment.setNbPages(preProcessedDocumentDetails.getDocument().getNbPages());

    // Produce kafka message to update flow and create flow document.
    this.streamBridge.send(
        KafkaUtils.UPDATE_FLOW_AFTER_PRE_TREATMENT_STEP_TOPIC_PORTAL, flowModelPreTreatment);

    log.info("Producing message to update FlowTraceability documents Error");
  }

  /** Produce message to update FLowTraceability step. */
  public void updateDepositPortalFlowStep(
      String uuid, int step, Optional<String> composedFileId, boolean validation) {
    var userInfo = this.keycloakService.getUserInfo();
    UpdateDepositFlowStepBuilder depositFlowStepBuilder =
        UpdateDepositFlowStep.builder()
            .uuid(uuid)
            .step(step)
            .fullName(userInfo.getFirstName().concat(" ".concat(userInfo.getLastName())))
            .status(FlowTraceabilityStatus.TO_FINALIZE.getValue())
            .validation(validation);
    composedFileId.ifPresent(depositFlowStepBuilder::composedId);
    this.streamBridge.send(KafkaUtils.UPDATE_DEPOSIT_FLOW_STEP, depositFlowStepBuilder.build());
  }

  public PortalFlowFileControl getDepositStep(String uuid, String token) {
    return this.fileCtrlMngtFeignClient.getPortalJsonFileControl(uuid, token);
  }

  @Autowired
  public void setFileCtrlMngtFeignClient(FileCtrlMngtFeignClient fileCtrlMngtFeignClient) {
    this.fileCtrlMngtFeignClient = fileCtrlMngtFeignClient;
  }

  /**
   * To cancel a flow in flow traceability.
   *
   * @param flowId refer to the uuid of JSON file control.
   */
  public void cancelFlow(String flowId) {
    final String authToken = PrivilegeValidationUtil.getAuthTokenWithPrefix();
    final PortalFlowFileControl depositStep = getDepositStep(flowId, authToken);
    final long ownerId = Long.parseLong(depositStep.getUserId());

    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_FLOW_TRACEABILITY,
        Privilege.SubCancelFlowTraceability.FLOW,
        false,
        ownerId);
    try {
      if (PortalDepositType.isPortalDepositCampaignType(depositStep.getFlow().getType())) {
        final String groupId =
            "CAMPAIGN_".concat(depositStep.getFlow().getSubChannel().toUpperCase());
        this.schedulerHandler.unScheduledJob(new JobTriggerDescriptor(flowId, groupId));

      } else {
        this.processControlSchedulerService.deleteSchedulerJobInfo(flowId); // remove unloading job
      }
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
    this.streamBridge.send(
        KafkaUtils.CANCEL_FLOW_TOPIC,
        new BaseUpdateFlowFromProcessCtrl(
            flowId,
            FlowTraceabilityStatusConstant.CANCELED,
            keycloakService.getUserInfo().getUsername(),
            ComputerSystemProduct.getDeviceId()));
  }

  private SchedulerHandler schedulerHandler;

  @Autowired
  public void setSchedulerHandler(SchedulerHandler schedulerHandler) {
    this.schedulerHandler = schedulerHandler;
  }

  /**
   * Handling process to validate flows.
   *
   * @param validationFlowRequest refers to the object of {@link ValidationFlowRequest}
   * @return the server name processing this action
   */
  public String validationFlow(ValidationFlowRequest validationFlowRequest) {
    if (validationFlowRequest.isValidate()) {
      final String username = this.keycloakService.getUserInfo().getUsername();
      this.executeValidationFlowScheduler(validationFlowRequest, username);
    }
    return CURRENT_SERVER_NAME;
  }

  /**
   * Prepare the required data for validation and set a schedule to proceed with the flow's
   * documents after validating.
   *
   * @param validationFlowRequest refers to the object of {@link ValidationFlowRequest}
   * @param username refers the identity of a user proceed this
   */
  @Async
  public void executeValidationFlowScheduler(
      ValidationFlowRequest validationFlowRequest, String username) {
    final List<String> flowIds = validationFlowRequest.getFlowIds();
    final List<String> composedIds = validationFlowRequest.getComposedIds();
    final String authToken =
        ProcessControlExecutionContextUtils.getBearerTokenWithPrefix(
            AuthenticationUtils.getAuthToken());
    var clientUnloadDetails = this.profileFeignClient.getClientUnloadDetails(authToken);
    if (clientUnloadDetails.getClientUnloads().isEmpty()) {
      throw new ClientUnloadingNotConfiguredException(clientUnloadDetails.getClientId());
    }
    // Get reference of authentication object of current thread for share with other/child thread
    final Authentication refAuthentication = SecurityContextHolder.getContext().getAuthentication();
    IntStream.range(0, validationFlowRequest.getFlowIds().size())
        .parallel()
        .forEach(
            index -> {
              // Set the authentication object of current background thread
              // with authentication object from parent thread
              SecurityContextHolder.getContext().setAuthentication(refAuthentication);
              depositedFlowScheduler(
                  flowIds.get(index),
                  composedIds.get(index),
                  username,
                  clientUnloadDetails,
                  validationFlowRequest.isValidate(),
                  Map.of(
                      FlowTreatmentConstants.FUNC_KEY,
                      CXM_ESPACE_VALIDATION,
                      FlowTreatmentConstants.PRIV_KEY,
                      ProfileConstants.EspaceValidation.VALIDATE_OR_REFUSE));
            });
  }

  @Async
  public void executeValidationFlow(
      ValidationFlowRequest validationFlowRequest,
      String username,
      String authToken,
      Date unloadingDate) {
    IntStream.range(0, validationFlowRequest.getFlowIds().size())
        .parallel()
        .forEach(
            index -> {
              final ExecutionContext context =
                  getValidationExecutionContext(validationFlowRequest, username, authToken, index);
              context.put(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, false);
              context.put(FlowTreatmentConstants.FORCE_UNLOADING_DATE, unloadingDate);
              try {
                this.manualProcessFlowValidation.execute(context);
              } catch (ExecutionException exception) {
                log.error(ERROR_MESSAGE, exception);
                context.put(
                    FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
                    ProcessControlStep.SWITCH);
                this.reportErrorToKafka(context);
              }
            });
  }

  private ExecutionContext getValidationExecutionContext(
      ValidationFlowRequest validationFlowRequest, String username, String authToken, int index) {
    return getValidationExecutionContext(
        validationFlowRequest, username, authToken, index, new ArrayList<>(), false);
  }

  private ExecutionContext getValidationExecutionContext(
      ValidationFlowRequest validationFlowRequest,
      String username,
      String authToken,
      int index,
      List<String> documentIds,
      boolean isValidateDocument) {
    final ExecutionContext context =
        getSendFlowContext(
            validationFlowRequest.getFlowIds().get(index),
            validationFlowRequest.getComposedIds().get(index),
            isValidateDocument,
            authToken);
    DepositedFlowLaunchRequest depositedFlowLaunchRequest = new DepositedFlowLaunchRequest();
    depositedFlowLaunchRequest.setDepositType(FlowTreatmentConstants.PORTAL_DEPOSIT);
    depositedFlowLaunchRequest.setUuid(validationFlowRequest.getFlowIds().get(index));
    depositedFlowLaunchRequest.setFileId(validationFlowRequest.getFlowIds().get(index));
    depositedFlowLaunchRequest.setUserName(username);
    context.put(FlowTreatmentConstants.DOCUMENT_ID, documentIds);
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlowLaunchRequest);
    context.put(FlowTreatmentConstants.CREATED_BY, username);
    context.put(FlowTreatmentConstants.IS_VALIDATE_ACTION, validationFlowRequest.isValidate());
    return context;
  }

  public String validateFlowDocument(
      ValidationFlowDocumentRequest validationFlowDocumentRequest, HttpHeaders headers) {
    if (validationFlowDocumentRequest.isValidate()) {
      final String username = this.keycloakService.getUserInfo().getUsername();
      var authToken = BearerAuthentication.getTokenWithPrefix(headers);
      var clientUnloadDetails = this.profileFeignClient.getClientUnloadDetails(authToken);
      if (clientUnloadDetails.getClientUnloads().isEmpty()) {
        throw new ClientUnloadingNotConfiguredException(clientUnloadDetails.getClientId());
      }
      this.executeValidationFlowDocumentsScheduler(
          validationFlowDocumentRequest, username, clientUnloadDetails);
    }
    return CURRENT_SERVER_NAME;
  }

  @Async
  public void executeValidationFlowDocumentsScheduler(
      ValidationFlowDocumentRequest validationFlowRequest,
      String username,
      SharedClientUnloadDetailsDTO clientUnloadDetails) {
    final String flowId = validationFlowRequest.getFlowId();
    final String composedFileId = validationFlowRequest.getComposedId();
    var context = this.getSendFlowContext(flowId, composedFileId);
    final DepositedFlowLaunchRequest depositedFlowLaunchRequest = new DepositedFlowLaunchRequest();
    depositedFlowLaunchRequest.setDepositType(FlowTreatmentConstants.PORTAL_DEPOSIT);
    depositedFlowLaunchRequest.setUuid(flowId);
    context.put(FlowTreatmentConstants.PORTAL_DEPOSIT, FlowTreatmentConstants.PORTAL_DEPOSIT);
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlowLaunchRequest);
    context.put(FlowTreatmentConstants.CREATED_BY, username);
    context.put(FlowTreatmentConstants.DOCUMENT_ID, validationFlowRequest.getDocumentIds());
    context.put(ProcessControlConstants.CLIENT_UNLOADING_DETAIL, clientUnloadDetails);
    context.put(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, true);
    context.putAll(
        Map.of(
            FlowTreatmentConstants.FUNC_KEY,
            CXM_ESPACE_VALIDATION,
            FlowTreatmentConstants.PRIV_KEY,
            ProfileConstants.EspaceValidation.VALIDATE_OR_REFUSE));
    try {
      manualFlowDocumentScheduler.execute(context);
    } catch (ExecutionException exception) {
      log.error(ERROR_MESSAGE, exception);
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.SWITCH);
      this.reportErrorToKafka(context);
    }
  }

  public void executeValidationFlowDocument(
      ValidationFlowDocumentRequest validationFlowRequest,
      String username,
      String authToken,
      Date unloadingDate) {
    final ExecutionContext context =
        getValidationExecutionContext(
            new ValidationFlowRequest(
                List.of(validationFlowRequest.getFlowId()),
                List.of(validationFlowRequest.getComposedId()),
                username,
                validationFlowRequest.isValidate()),
            username,
            authToken,
            0,
            validationFlowRequest.getDocumentIds(),
            true);
    context.put(FlowTreatmentConstants.BEARER_TOKEN, authToken);
    context.put(FlowTreatmentConstants.FORCE_UNLOADING_DATE, unloadingDate);
    try {
      this.manualProcessFlowDocumentValidation.execute(context);
    } catch (ExecutionException exception) {
      log.error(ERROR_MESSAGE, exception);
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.SWITCH);
      this.reportErrorToKafka(context);
    }
  }

  public Map<String, String> checkUserPrivilege(boolean isModify, long ownerId) {
    final String funcKey = ProfileConstants.CXM_FLOW_DEPOSIT;
    String privKey = "";
    if (isModify) {
      privKey = FlowDepositArea.MODIFY_A_DEPOSIT;
      PrivilegeValidationUtil.validateUserAccessPrivilege(funcKey, privKey, false, ownerId);
    } else {
      privKey = FlowDepositArea.SEND_A_LETTER;
      PrivilegeValidationUtil.getUserPrivilegeDetails(
          funcKey, FlowDepositArea.SEND_A_LETTER, false, false);
    }
    return Map.of(
        FlowTreatmentConstants.FUNC_KEY, funcKey, FlowTreatmentConstants.PRIV_KEY, privKey);
  }

  public DepositStepResponse getDepositStepResponse(String uuid, String token, int step) {
    DepositStepResponse depositStepResponse =
        new DepositStepResponse(
            this.fileCtrlMngtFeignClient.getPortalJsonFileControl(uuid, token), step);

    Map<String, ResourceFile> resourceFileMap =
        this.resourceFileService.getResourceBackground(uuid).stream()
            .collect(
                Collectors.toMap(ResourceFile::getPosition, backgroundFile -> backgroundFile));
    if (Objects.nonNull(depositStepResponse.getTreatmentResponse())) {
      ProcessingResponse treatmentResponse = depositStepResponse.getTreatmentResponse();

      BackgroundPage backgroundPage = this.getBackgroundPageResource(resourceFileMap,
          treatmentResponse);
      Attachments attachments = this.getAttachmentResource(resourceFileMap, treatmentResponse);

      var signature = getSigntureName(resourceFileMap,treatmentResponse);

      treatmentResponse.setAttachments(attachments);
      treatmentResponse.setBackgroundPage(backgroundPage);
      treatmentResponse.setSignature(signature);
      depositStepResponse.setTreatmentResponse(treatmentResponse);
    }

    return depositStepResponse;
  }

  public Map<String, Boolean> reIdentifyDepositedFlow(String uuid, String token) {
    final PortalFlowFileControl portalJson =
        this.getDepositStep(uuid, BearerAuthentication.PREFIX_TOKEN.concat(token));
    final long ownerId = PrivilegeValidationUtil.getUserDetail().getOwnerId();
    final Map<String, String> privilege = this.checkUserPrivilege(false, ownerId);
    var processingContext = this.getFlowIdentifyingPayload(portalJson, token);
    processingContext.putAll(privilege);
    processingContext.put(FlowTreatmentConstants.OWNER_ID, ownerId);
    this.manualReIdentifierFlow.execute(processingContext);
    var isModelChanged =
        !portalJson
            .getFlow()
            .getModelName()
            .equals(processingContext.get(FlowTreatmentConstants.MODEL_NAME, String.class));
    try {
      return Map.of("isModelChanged", isModelChanged);
    } catch (Exception exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(processingContext);
      return Map.of("isModelChanged", false);
    }
  }

  private BackgroundPage getBackgroundPageResource(Map<String, ResourceFile> resourceFileMap,
      ProcessingResponse treatmentResponse) {

    BackgroundPage backgroundPage =
        ObjectUtils.defaultIfNull(treatmentResponse.getBackgroundPage(), new BackgroundPage());
    if (resourceFileMap.containsKey(BackgroundPosition.ALL_PAGES.name())) {
      ResourceFile resourceFile = resourceFileMap.get(BackgroundPosition.ALL_PAGES.name());
      backgroundPage.setBackground(resourceFile.getOriginalName());
      backgroundPage.setPosition(resourceFile.getPosition());
    } else {
      if (resourceFileMap.containsKey(BackgroundPosition.FIRST_PAGE.name())) {
        ResourceFile resourceFile =
            resourceFileMap.get(BackgroundPosition.FIRST_PAGE.name());
        backgroundPage.setBackgroundFirst(resourceFile.getOriginalName());
        backgroundPage.setPositionFirst(resourceFile.getPosition());
      }

      if (resourceFileMap.containsKey(BackgroundPosition.NEXT_PAGES.name())) {
        ResourceFile resourceFile =
            resourceFileMap.get(BackgroundPosition.NEXT_PAGES.name());
        backgroundPage.setBackground(resourceFile.getOriginalName());
        backgroundPage.setPosition(resourceFile.getPosition());
      }

      if (resourceFileMap.containsKey(BackgroundPosition.LAST_PAGE.name())) {
        ResourceFile resourceFile =
            resourceFileMap.get(BackgroundPosition.LAST_PAGE.name());
        backgroundPage.setBackgroundLast(resourceFile.getOriginalName());
        backgroundPage.setPositionLast(resourceFile.getPosition());
      }
    }
    return backgroundPage;
  }

  private Attachments getAttachmentResource(Map<String, ResourceFile> resourceFileMap,
      ProcessingResponse treatmentResponse) {

    Attachments attachments =
        ObjectUtils.defaultIfNull(treatmentResponse.getAttachments(), new Attachments());
    if (resourceFileMap.containsKey(AttachmentPosition.FIRST_POSITION.name())) {
      ResourceFile resourceFile =
          resourceFileMap.get(AttachmentPosition.FIRST_POSITION.name());
      attachments.setAttachment1(resourceFile.getOriginalName());
    }
    if (resourceFileMap.containsKey(AttachmentPosition.SECOND_POSITION.name())) {
      ResourceFile resourceFile =
          resourceFileMap.get(AttachmentPosition.SECOND_POSITION.name());
      attachments.setAttachment2(resourceFile.getOriginalName());
    }
    if (resourceFileMap.containsKey(AttachmentPosition.THIRD_POSITION.name())) {
      ResourceFile resourceFile =
          resourceFileMap.get(AttachmentPosition.THIRD_POSITION.name());
      attachments.setAttachment3(resourceFile.getOriginalName());
    }
    if (resourceFileMap.containsKey(AttachmentPosition.FOURTH_POSITION.name())) {
      ResourceFile resourceFile =
          resourceFileMap.get(AttachmentPosition.FOURTH_POSITION.name());
      attachments.setAttachment4(resourceFile.getOriginalName());
    }
    if (resourceFileMap.containsKey(AttachmentPosition.FIFTH_POSITION.name())) {
      ResourceFile resourceFile =
          resourceFileMap.get(AttachmentPosition.FIFTH_POSITION.name());
      attachments.setAttachment5(resourceFile.getOriginalName());
    }
    return attachments;
  }

  private String getSigntureName(Map<String, ResourceFile> resourceFileMap,ProcessingResponse treatmentResponse) {
    var signature = treatmentResponse.getSignature();

    if(StringUtils.isNotBlank(signature)) {
      var fileName = FilenameUtils.removeExtension(FilenameUtils.getName(signature));

      var found = resourceFileMap.values().stream().filter(x -> x.getFileId().equals(fileName)).findFirst();
      if(found.isPresent()) return found.get().getOriginalName();

      return fileName;
    }
    
    return Strings.EMPTY;
  }
}
