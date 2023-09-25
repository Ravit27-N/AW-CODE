package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.ms5.constant.Functionality;
import com.tessi.cxm.pfl.ms5.entity.Privilege;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Builder
@Setter
@Getter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileDetailDto implements Serializable {

  private long id;

  @Size(max = 128)
  @NotEmpty()
  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      example = "cxm_template",
      implementation = Functionality.class)
  private String functionalityKey;

  @JsonProperty private List<Privilege> privileges;

  @Schema(type = "String", required = true, example = "user")
  private String visibilityLevel;

  @Schema(type = "String", required = true, example = "owner")
  private String modificationLevel;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProfileDetailDto)) {
      return false;
    }
    ProfileDetailDto that = (ProfileDetailDto) o;
    return getId() == that.getId() && getFunctionalityKey().equals(that.getFunctionalityKey());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFunctionalityKey());
  }
}
