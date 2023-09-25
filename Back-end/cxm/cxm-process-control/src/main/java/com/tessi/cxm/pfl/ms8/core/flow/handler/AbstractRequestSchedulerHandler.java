package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.util.CollectionUtils;

@Slf4j
public abstract class AbstractRequestSchedulerHandler extends AbstractExecutionHandler {
  @SuppressWarnings("unchecked")
  protected <T extends List<?>> T cast(Object obj) {
    return (T) obj;
  }

  private final StreamBridge streamBridge;

  protected AbstractRequestSchedulerHandler(StreamBridge streamBridge) {
    this.streamBridge = streamBridge;
  }
  /** Produce message to update flow and flow document to status schedule. */
  protected void updateFlowToScheduled(UpdateFlowStatusModel updateFlowStatus) {
    final boolean sent =
        this.streamBridge.send(KafkaUtils.UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC, updateFlowStatus);
    if (sent) {
      log.info(
          "Flow validation is updated to the topic {}",
          KafkaUtils.UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC);
    }
  }

  protected void normalizeValidationDocument(ExecutionContext context) {
    List<String> docIds = cast(context.get(FlowTreatmentConstants.DOCUMENT_ID, Object.class));
    if (CollectionUtils.isEmpty(docIds)) {
      return;
    }
    final PortalFlowFileControl validationFlow =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    final List<PortalFileFlowDocument> normalizedDocument =
        validationFlow.getFlow().getFlowDocuments().stream()
            .filter(flowDocument -> docIds.contains(flowDocument.getUuid()))
            .collect(Collectors.toList());
    validationFlow.getFlow().setFlowDocuments(normalizedDocument);
    context.put(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, validationFlow);
  }
}
