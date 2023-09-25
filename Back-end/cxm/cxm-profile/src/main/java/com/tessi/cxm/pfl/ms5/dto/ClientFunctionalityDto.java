package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.PrivilegeKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.entity.Privilege;
import com.tessi.cxm.pfl.ms5.constant.Functionality;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.exception.ModificationLevelNotExistException;
import com.tessi.cxm.pfl.shared.exception.VisibilityLevelNotExistException;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.service.restclient.VisibilityLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Set;
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
public class ClientFunctionalityDto implements Serializable {
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

  @JsonProperty private Set<Privilege> privileges;

  @Schema(type = "String", required = true, example = "user")
  private String visibilityLevel;

  @Schema(type = "String", required = true, example = "owner")
  private String modificationLevel;

  public void setFunctionalityKey(String functionalityKey) {
    if (!Functionality.keyExists(functionalityKey, true)) {
      throw new FunctionalityKeyNotFoundException(functionalityKey);
    }
    this.functionalityKey = functionalityKey;
  }

  public void setPrivileges(Set<Privilege> privileges) {
    this.validatePrivileges(privileges); // check exist privileges
    this.privileges = privileges;
  }

  private void validatePrivileges(Set<Privilege> privileges) {
    privileges.forEach(
        privilege -> {
          if (!PrivilegeKeyValidator.keyExists(privilege.getKey())) {
            throw new PrivilegeKeyNotFoundException(privilege.getKey());
          }
          if (!VisibilityLevel.keyExists(privilege.getVisibilityLevel())) {
            throw new VisibilityLevelNotExistException(privilege.getVisibilityLevel());
          }

          if (!ModificationLevel.keyExists(privilege.getModificationLevel())) {
            throw new ModificationLevelNotExistException(privilege.getModificationLevel());
          }
        });
  }
}
