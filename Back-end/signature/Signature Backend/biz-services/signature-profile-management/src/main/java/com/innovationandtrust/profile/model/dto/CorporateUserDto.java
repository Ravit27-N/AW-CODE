package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CorporateUserDto extends UserEmployee implements Serializable {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Set<String> roles = new HashSet<>();

  @Min(1)
  private long companyId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CorporateUserDto that)) return false;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
