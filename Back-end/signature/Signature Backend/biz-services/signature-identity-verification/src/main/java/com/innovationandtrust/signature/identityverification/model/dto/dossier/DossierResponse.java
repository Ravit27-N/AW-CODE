package com.innovationandtrust.signature.identityverification.model.dto.dossier;

import com.innovationandtrust.signature.identityverification.model.model.dossier.Dossier;

import java.util.Objects;

import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Response class for dossier. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DossierResponse {
  private String firstname;
  private String participantUuid;
  private String tel;
  private String dossierName;
  private String status;
  private String currentStep;
  private VerificationChoice verificationChoice;

  /**
   * Constructor for dossier response.
   *
   * @param dossier dossier
   */
  public DossierResponse(Dossier dossier) {
    this.firstname = dossier.getFirstname();
    String phone = dossier.getTel();
    this.tel = phone.substring(0, phone.length() - 4).concat("...");
    this.dossierName = dossier.getDossierName();
    this.verificationChoice = dossier.getVerificationChoice();
    this.status = dossier.getStatus().name();
    this.currentStep = dossier.getUserInCurrentStep().name();
  }
}
