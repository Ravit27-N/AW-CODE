package com.tessi.cxm.pfl.ms8.model;

import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowTreatmentFlowRequest implements Serializable {
  @NotEmpty private String composedFileId;
  @NotEmpty private String idCreator;
  @NotEmpty private String uuid;
  @NotNull private PortalFlowFileControl fileControl;
}
