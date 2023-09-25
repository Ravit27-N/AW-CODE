package com.tessi.cxm.pfl.ms8.model;

import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchFlowResponse implements Serializable {

  private String filename;
  private int nbDocuments;
  private int nbPages;
  private int nbFailed;
  private List<FlowDocumentProduction> productions;

  public SwitchFlowResponse(PortalFlowFileControl flowFileControl) {
    this.filename = flowFileControl.getFileName();
    var flow = flowFileControl.getFlow();
    this.nbDocuments = Integer.parseInt(flow.getNbDocuments());
    this.nbPages = Integer.parseInt(flow.getNbPages());
    this.buildProductions(flow.getFlowDocuments());
  }

  private void buildProductions(List<PortalFileFlowDocument> flowDocuments) {
    this.productions =
        flowDocuments.stream().map(this::buildProduction).collect(Collectors.toList());
  }

  private FlowDocumentProduction buildProduction(PortalFileFlowDocument flowDocument) {
    var production = flowDocument.getProduction();
    return FlowDocumentProduction.builder()
        .urgency(production.getUrgency())
        .recto(production.getRecto())
        .wrap(production.getWrap())
        .color(production.getColor())
        .build();
  }
}
