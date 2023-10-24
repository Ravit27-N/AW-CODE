package com.innovationandtrust.share.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvitationStatusConstant {

  //En attente
  public static final Integer ON_HOLD = 1;

  // Envoyé
  public static final Integer SENT = 2;

  //En cours
  public static final Integer IN_PROGRESS = 3;

  //Signé
  public static final Integer SIGNED = 4;

}
