package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import com.tessi.cxm.pfl.ms8.service.ProcessControlService;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ProcessScheduleJobHandler extends AbstractProcessScheduleJobInfoHandler {

  private ProcessControlService processControlService;

  private Date unloadingDate;

  public ProcessScheduleJobHandler(KeycloakService keycloakService) {
    super(keycloakService);
  }

  @Autowired
  @Lazy
  public void setProcessControlService(ProcessControlService processControlService) {
    this.processControlService = processControlService;
  }

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *                for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    this.unloadingDate = context.get(FlowTreatmentConstants.FORCE_UNLOADING_DATE, Date.class);
    // note :: UnloadingScheduleJob have two types (By validation and none validation)
    final Map<Boolean, List<UnloadingScheduleJob>> groupJobInfos = getGroupJobInfo(context);
    splitProcessSchedulerJob(groupJobInfos);
    return ExecutionState.NEXT;
  }

  private void splitProcessSchedulerJob(Map<Boolean, List<UnloadingScheduleJob>> groupJobInfos) {
    groupJobInfos.entrySet().stream()
        .parallel()
        .forEach(
            groupJobEntry -> {
              if (Boolean.TRUE.equals(groupJobEntry.getKey())) {
                executeDepositFlowValidation(groupJobEntry.getValue());
              } else {
                executeDepositSwitchFlow(groupJobEntry.getValue());
              }
            });
  }

  private void executeDepositSwitchFlow(List<UnloadingScheduleJob> unloadingScheduleJobs) {
    final String authToken = getAuthToken();

    unloadingScheduleJobs.stream()
        .parallel()
        .forEach(
            job ->
                this.processControlService.switchDepositedFlow(
                    job.getFlowId(), job.getComposedFileId(), authToken, job.getIdCreator(), true,
                    unloadingDate));
  }

  private void execFlowValidation(
      List<ValidationFlowRequest> validationFlowRequest, String authToken) {
    validationFlowRequest.stream()
        .parallel()
        .forEach(
            flowRequest ->
                this.processControlService.executeValidationFlow(
                    flowRequest, flowRequest.getCreatedBy(), authToken, unloadingDate));
  }

  private void execFlowDocumentValidation(
      List<ValidationFlowDocumentRequest> validationFlowDocumentRequests, String authToken) {
    validationFlowDocumentRequests.stream()
        .parallel()
        .forEach(
            documentRequest ->
                this.processControlService.executeValidationFlowDocument(
                    documentRequest, documentRequest.getCreatedBy(), authToken, unloadingDate));
  }

  private void executeDepositFlowValidation(List<UnloadingScheduleJob> unloadingScheduleJobs) {
    final Map<String, Object> executionValidationFlow =
        getExecutionValidationFlow(unloadingScheduleJobs);
    final String authToken = getAuthToken();
    executionValidationFlow.entrySet().stream()
        .parallel()
        .forEach(
            validationFlow -> {
              if (NONE_DOCUMENT.equals(validationFlow.getKey())) {
                execFlowDocumentValidation(cast(validationFlow.getValue()), authToken);
              } else {
                execFlowValidation(cast(validationFlow.getValue()), authToken);
              }
            });
  }
}
