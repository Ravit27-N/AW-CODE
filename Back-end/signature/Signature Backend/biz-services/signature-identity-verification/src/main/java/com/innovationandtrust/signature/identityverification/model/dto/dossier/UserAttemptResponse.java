package com.innovationandtrust.signature.identityverification.model.dto.dossier;

import com.innovationandtrust.signature.identityverification.model.model.dossier.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Response class for user attempt. */
@Getter
@Setter
@Builder
public class UserAttemptResponse {
  private int attempts;
  private Status status;
}
