package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.innovationandtrust.share.model.corporateprofile.BusinessUnitRes;
import com.innovationandtrust.share.model.corporateprofile.UserAccessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NormalUserDto extends UserEmployee implements Serializable {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Builder.Default
  private Set<String> roles = new HashSet<>();

  private Boolean active;

  @Schema(hidden = true)
  private BusinessUnitInfo businessUnitInfo;

  @JsonProperty(access = Access.READ_ONLY)
  public BusinessUnitInfo getBusinessUnitInfo() {
    return this.businessUnitInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NormalUserDto that)) return false;
    return this.getId() == that.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
