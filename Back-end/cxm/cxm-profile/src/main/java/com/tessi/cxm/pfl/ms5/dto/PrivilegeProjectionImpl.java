package com.tessi.cxm.pfl.ms5.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeProjectionImpl implements PrivilegeProjection {

  private String key;
  private String visibilityLevel;
  private String modificationLevel;
  private List<Long> modificationOwners;
  private List<Long> visibilityOwners;

  @Override
  public String getKey() {
    return this.key;
  }

  @Override
  public String getVisibilityLevel() {
    return this.visibilityLevel;
  }

  @Override
  public String getModificationLevel() {
    return this.modificationLevel;
  }
}
