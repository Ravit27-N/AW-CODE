package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.share.model.corporateprofile.BusinessUnitRes;
import com.innovationandtrust.share.model.corporateprofile.UserAccessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserEmployee extends AbstractUser {
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long userAccessId;

  @NotNull
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long businessId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private UserAccessDTO userAccess;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BusinessUnitRes businessUnit;

  @Schema(hidden = true)
  private CompanyInfo company;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public CompanyInfo getCompany() {
    return this.company;
  }

  public void setUserAccessId(Long userAccessId) {
    this.userAccessId = userAccessId;
  }
}
