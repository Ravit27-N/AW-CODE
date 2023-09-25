package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import com.tessi.cxm.pfl.ms8.service.ProcessControlSchedulerService;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.COMPOSED_FILE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetUnloadingScheduleJobHandler extends AbstractExecutionHandler {

  public static final String EXISTS_UNLOADING_SCH_DOCS_ID = "exist_unloading_sch_docs_id";

  public static final String IS_EXISTS_UNLOADING_SCH_DOCS_ID = "is_exist_unloading_sch_docs_id";
  private final ProcessControlSchedulerService processControlSchedulerService;

  @SuppressWarnings("unchecked")
  private static <T extends List<?>> T cast(Object obj) {
    return (T) obj;
  }
  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {

    final UnloadingScheduleJob unloadingScheduleJob = getUnloadingScheduleJob(context);
    setUnloadingScheduleJob(unloadingScheduleJob);
    return ExecutionState.NEXT;
  }

  private UnloadingScheduleJob getNewUnloadingScheduleJob(ExecutionContext context) {
    final String uuid = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
    final String composedFileId = context.get(COMPOSED_FILE_ID, String.class);
    final String username = context.get(FlowTreatmentConstants.CREATED_BY, String.class);
    final boolean isValidationDocument =
        BooleanUtils.toBoolean(
            context.get(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, Boolean.class));
    final SharedClientUnloadDetailsDTO sharedClientUnloadDetailsDTO =
        context.get(
            ProcessControlConstants.CLIENT_UNLOADING_DETAIL, SharedClientUnloadDetailsDTO.class);
    UnloadingScheduleJob unloadingScheduleJob = new UnloadingScheduleJob();
    unloadingScheduleJob.setFlowId(uuid);
    unloadingScheduleJob.setClientId(sharedClientUnloadDetailsDTO.getClientId());
    unloadingScheduleJob.setComposedFileId(composedFileId);
    unloadingScheduleJob.setCreatedDate(new Date());
    unloadingScheduleJob.setIdCreator(username);
    unloadingScheduleJob.setValidation(isValidationDocument);
    unloadingScheduleJob.setDocumentIds(new ArrayList<>());
    return unloadingScheduleJob;
  }

  public void setUnloadingScheduleJob(UnloadingScheduleJob unloadingScheduleJob) {
    processControlSchedulerService.createSchedulerJobInfo(unloadingScheduleJob);
  }

  public UnloadingScheduleJob getUnloadingScheduleJob(ExecutionContext context) {
    final PortalFlowFileControl validationFlow =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    final String uuid = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
    final List<String> documentIds =
        cast(context.get(FlowTreatmentConstants.DOCUMENT_ID, Object.class));
    final UnloadingScheduleJob unloadingScheduleJob =
        processControlSchedulerService
            .getUnloadingScheduleJob(uuid)
            .orElse(this.getNewUnloadingScheduleJob(context));
    final List<String> jobDocumentIds = unloadingScheduleJob.getDocumentIds();
    context.put(EXISTS_UNLOADING_SCH_DOCS_ID, jobDocumentIds);
    if (!CollectionUtils.isEmpty(documentIds)
        && validationFlow.getFlow().getFlowDocuments().size() != documentIds.size()) {
      jobDocumentIds.addAll(documentIds);
    } else if (!CollectionUtils.isEmpty(jobDocumentIds)) {
      final List<String> newUnloadingDocumentIds =
          getNewUnloadingDocumentIds(validationFlow, jobDocumentIds);
      context.put(FlowTreatmentConstants.DOCUMENT_ID, newUnloadingDocumentIds);
      context.put(IS_EXISTS_UNLOADING_SCH_DOCS_ID, true);
      jobDocumentIds.addAll(newUnloadingDocumentIds);
    }
    return unloadingScheduleJob;
  }

  private List<String> getNewUnloadingDocumentIds(
      PortalFlowFileControl fileControl, List<String> existingDocumentIds) {
    return fileControl.getFlow().getFlowDocuments().stream()
        .map(PortalFileFlowDocument::getUuid)
        .filter(docId -> !existingDocumentIds.contains(docId))
        .collect(Collectors.toList());
  }
}
