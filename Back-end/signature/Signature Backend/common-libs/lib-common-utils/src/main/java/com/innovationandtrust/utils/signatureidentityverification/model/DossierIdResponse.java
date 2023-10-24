package com.innovationandtrust.utils.signatureidentityverification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Response class for dossier id. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DossierIdResponse {
  private String dossierId;
  private String participantUuid;
}
