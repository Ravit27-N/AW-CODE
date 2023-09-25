package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.service.restclient.VisibilityLevel;
import com.vladmihalcea.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(typeClass = JsonType.class)
@Builder
public class Privilege implements Serializable {

  @Schema(type = "string", example = "cxm_template_list")
  private String key;

  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      example = "user",
      implementation = VisibilityLevel.class)
  private String visibilityLevel = "";

  private boolean isVisibility;

  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      example = "owner",
      implementation = ModificationLevel.class)
  private String modificationLevel = "";

  private boolean isModification;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Privilege)) {
      return false;
    }
    Privilege privilege = (Privilege) o;

    return key.equals(privilege.key)
        && Objects.equals(this.modificationLevel, privilege.getModificationLevel())
        && Objects.equals(this.visibilityLevel, privilege.getVisibilityLevel());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, modificationLevel, visibilityLevel);
  }
}
