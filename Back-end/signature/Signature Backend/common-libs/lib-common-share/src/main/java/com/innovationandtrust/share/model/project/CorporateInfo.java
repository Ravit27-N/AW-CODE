package com.innovationandtrust.share.model.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateInfo {
  private long settingId;
  private String companyUuid;
  private long companyId;
  private String companyName;
  private String mainColor;
  private String secondaryColor;
  private String linkColor;
  private String logo;

  public CorporateInfo(
      long settingId,
      long companyId,
      String companyName,
      String mainColor,
      String secondaryColor,
      String linkColor,
      String logo,
      String companyUuid) {
    this.settingId = settingId;
    this.companyId = companyId;
    this.companyName = companyName;
    this.mainColor = mainColor;
    this.secondaryColor = secondaryColor;
    this.linkColor = linkColor;
    this.logo = logo;
    this.companyUuid = companyUuid;
  }
}
