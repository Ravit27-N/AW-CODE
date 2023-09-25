package com.tessi.cxm.pfl.ms8.util;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

public class FlowValidationUtils {

  private FlowValidationUtils() {
    // do nothing
  }

  public static void normalizeValidationDocument(ExecutionContext context) {
    var docIds = context.get(FlowTreatmentConstants.DOCUMENT_ID, List.class);
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
