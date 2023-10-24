package com.innovationandtrust.share.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectStatusConstant {

  // Brouillon
  public static final Integer DRAFT = 1;

  // En cours
  public static final Integer IN_PROGRESS = 2;

  // Terminé
  public static final Integer COMPLETED = 3;

  // Refusé
  public static final Integer REFUSED = 4;

  // Expiré
  public static final Integer EXPIRED = 5;

  // Urgence
  public static final Integer URGENT = 6;

  public static final Integer ABANDON = 7;
}
