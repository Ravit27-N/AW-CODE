package com.tessi.cxm.pfl.ms5.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileProjectionImpl implements UserProfileProjection {

  private String functionalityKey;

  private List<PrivilegeProjection> privileges;

  private String visibilityLevel;

  private String modificationLevel;

  @Override
  public String getFunctionalityKey() {
    return this.functionalityKey;
  }

  @Override
  public String getModificationLevel() {
    return this.modificationLevel;
  }

  @Override
  public String getVisibilityLevel() {
    return this.visibilityLevel;
  }

  @Override
  public List<PrivilegeProjection> getPrivileges() {
    return this.privileges;
  }
}
