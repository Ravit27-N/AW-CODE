package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractProcessScheduleJobInfoHandler extends AbstractExecutionHandler {

  protected static final String NONE_DOCUMENT = "#NONE_DOCUMENT";
  protected static final String DOCUMENT = "#DOCUMENT";
  private final TokenManager tokenManager;

  protected AbstractProcessScheduleJobInfoHandler(KeycloakService keycloakService) {
    this.tokenManager = keycloakService.getTokenManager();
  }

  @SuppressWarnings("unchecked")
  protected static <T extends List<?>> T cast(Object obj) {
    return (T) obj;
  }

  protected Map<Boolean, List<UnloadingScheduleJob>> getGroupJobInfo(ExecutionContext context) {
    List<UnloadingScheduleJob> unloadingScheduleJobs =
        cast(context.get(FlowTreatmentConstants.SCHEDULER_JOB_INFO, Object.class));
    return unloadingScheduleJobs.stream()
        .collect(Collectors.groupingBy(UnloadingScheduleJob::isValidation));
  }

  protected List<ValidationFlowRequest> getValidationFlowRequest(
      List<UnloadingScheduleJob> unloadingScheduleJobs) {
    final Map<String, List<UnloadingScheduleJob>> groupJobByIdCreator =
        unloadingScheduleJobs.stream()
            .filter(
                unloadingSchedule -> CollectionUtils.isEmpty(unloadingSchedule.getDocumentIds()))
            .collect(Collectors.groupingBy(UnloadingScheduleJob::getIdCreator));

    return groupJobByIdCreator.entrySet().stream()
        .map(
            map ->
                new ValidationFlowRequest(
                    getFlowIds(map.getValue()), getComposedIds(map.getValue()), map.getKey(), true))
        .collect(Collectors.toList());
  }

  protected List<String> getFlowIds(List<UnloadingScheduleJob> unloadingScheduleJobs) {
    return unloadingScheduleJobs.stream()
        .map(UnloadingScheduleJob::getFlowId)
        .collect(Collectors.toList());
  }

  protected List<String> getComposedIds(List<UnloadingScheduleJob> unloadingScheduleJobs) {
    return unloadingScheduleJobs.stream()
        .map(UnloadingScheduleJob::getComposedFileId)
        .collect(Collectors.toList());
  }

  protected Map<String, Object> getExecutionValidationFlow(
      List<UnloadingScheduleJob> unloadingScheduleJobs) {
    final List<ValidationFlowDocumentRequest> validationFlowDocumentRequests =
        getValidationFlowDocumentRequests(unloadingScheduleJobs);
    final List<ValidationFlowRequest> validationFlowRequest =
        getValidationFlowRequest(unloadingScheduleJobs);
    return Map.of(DOCUMENT, validationFlowRequest, NONE_DOCUMENT, validationFlowDocumentRequests);
  }

  protected List<ValidationFlowDocumentRequest> getValidationFlowDocumentRequests(
      List<UnloadingScheduleJob> unloadingScheduleJobs) {
    return unloadingScheduleJobs.stream()
        .filter(jobInfo -> !CollectionUtils.isEmpty(jobInfo.getDocumentIds()))
        .map(
            data ->
                new ValidationFlowDocumentRequest(
                    data.getFlowId(),
                    data.getComposedFileId(),
                    data.getIdCreator(),
                    data.getDocumentIds(),
                    true))
        .collect(Collectors.toList());
  }

  protected String getAuthToken() {
    return this.tokenManager.getAccessToken().getToken();
  }
}
