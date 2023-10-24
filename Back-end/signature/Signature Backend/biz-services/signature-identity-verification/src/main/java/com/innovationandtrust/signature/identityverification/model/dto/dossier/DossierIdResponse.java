package com.innovationandtrust.signature.identityverification.model.dto.dossier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Response class for dossier id. */
@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class DossierIdResponse {
  private String dossierId;
  private String participantUuid;
}
