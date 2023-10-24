package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto extends AbstractUser implements Serializable {
  private Set<String> roles = new HashSet<>();
  @NotEmpty private Long companyId;
  private String corporateUuid = "";
  private Boolean active;

  @Schema(hidden = true)
  private Boolean deleted;

  private Set<String> test;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserDto userDto)) {
      return false;
    }
    return getId() == userDto.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
