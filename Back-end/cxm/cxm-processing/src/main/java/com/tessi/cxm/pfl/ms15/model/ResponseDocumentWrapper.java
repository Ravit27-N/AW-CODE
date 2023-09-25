package com.tessi.cxm.pfl.ms15.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProcessing;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDocumentWrapper implements Serializable {
  private List<PortalFileDocumentProcessing> documentProcessing;
  private String composedFileId;
}
