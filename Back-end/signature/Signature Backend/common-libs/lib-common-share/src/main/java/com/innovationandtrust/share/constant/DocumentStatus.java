package com.innovationandtrust.share.constant;

import lombok.Getter;

public enum DocumentStatus {
  IN_PROGRESS(DocumentStatusConstant.IN_PROGRESS, "In progress", "En cours"),
  SIGNED(DocumentStatusConstant.SIGNED, "Signed", "Signé"),
  APPROVED(DocumentStatusConstant.APPROVED, "Approved", "Approuvé"),
  RECEIVED(DocumentStatusConstant.RECEIVED, "Received", "Reçu"),
  REFUSED(DocumentStatusConstant.REFUSED, "Refused", "Refusé"),
  READ(DocumentStatusConstant.READ, "Read", "Lu");

  @Getter private final Integer status;
  @Getter private final String en;
  @Getter private final String fr;

  DocumentStatus(Integer status, String en, String fr) {
    this.status = status;
    this.en = en;
    this.fr = fr;
  }
}
