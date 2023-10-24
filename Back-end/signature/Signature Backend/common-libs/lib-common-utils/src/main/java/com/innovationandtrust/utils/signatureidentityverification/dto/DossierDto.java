package com.innovationandtrust.utils.signatureidentityverification.dto;

import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.style.ToStringCreator;

/** Request class for dossier dto. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DossierDto {
  private String dossierId;
  private String firstname;
  private String tel;
  private String participantUuid;
  private String dossierName;
  private VerificationChoice verificationChoice;

  @Override
  public String toString() {
    return new ToStringCreator(this)
        .append("firstname", firstname)
        .append("participantUuid", participantUuid)
        .append("tel", tel)
        .append("dossierName", dossierName)
        .append("verificationChoice", verificationChoice)
        .toString();
  }
}
