package com.innovationandtrust.share.model.corporateprofile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CorporateSettingDto implements Serializable {
  private Long id;

  @Min(1)
  private Long companyId;

  private String companyUuid;

  @NotEmpty private String mainColor;
  private String secondaryColor;
  private String linkColor;

  private String logo;

  private boolean isDefault = false;
}
