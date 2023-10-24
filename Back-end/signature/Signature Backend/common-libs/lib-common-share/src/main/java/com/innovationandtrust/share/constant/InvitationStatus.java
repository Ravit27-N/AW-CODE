package com.innovationandtrust.share.constant;

import lombok.Getter;

public enum InvitationStatus {
  ON_HOLD(InvitationStatusConstant.ON_HOLD, "On hold", "En attente"),
  SENT(InvitationStatusConstant.SENT, "Sent", "Envoyé"),
  IN_PROGRESS(InvitationStatusConstant.IN_PROGRESS, "In progress", "En cours"),
  SIGNED(InvitationStatusConstant.SIGNED, "Signed", "Signé");

  @Getter private final Integer status;

  @Getter private final String en;

  @Getter private final String fr;

  InvitationStatus(Integer status, String en, String fr) {
    this.status = status;
    this.en = en;
    this.fr = fr;
  }
}
