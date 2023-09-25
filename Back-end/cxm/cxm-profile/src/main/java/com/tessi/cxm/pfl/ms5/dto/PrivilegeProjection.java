package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PrivilegeProjectionImpl.class)
public interface PrivilegeProjection {
  String getKey();

  String getVisibilityLevel();

  String getModificationLevel();
}
