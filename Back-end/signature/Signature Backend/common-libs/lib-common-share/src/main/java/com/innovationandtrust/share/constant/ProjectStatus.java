package com.innovationandtrust.share.constant;

import lombok.Getter;

public enum ProjectStatus {
  DRAFT(ProjectStatusConstant.DRAFT, "Draft", "Brouillon"),
  IN_PROGRESS(ProjectStatusConstant.IN_PROGRESS, "In progress", "En cours"),
  COMPLETED(ProjectStatusConstant.COMPLETED, "Completed", "Terminé"),
  REFUSED(DocumentStatusConstant.REFUSED, "Refused", "Refusé"),
  EXPIRED(ProjectStatusConstant.EXPIRED, "Expired", "Expiré"),
  URGENT(ProjectStatusConstant.URGENT, "Urgent", "Urgence"),
  ABANDON(ProjectStatusConstant.ABANDON, "Abandon", "Abandon");

  @Getter private final Integer status;

  @Getter private final String en;

  @Getter private final String fr;

  ProjectStatus(Integer status, String en, String fr) {
    this.status = status;
    this.en = en;
    this.fr = fr;
  }
}
