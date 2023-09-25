package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(as = UserProfileProjectionImpl.class)
public interface UserProfileProjection {

  String getFunctionalityKey();

  String getModificationLevel();

  String getVisibilityLevel();

  List<PrivilegeProjection> getPrivileges();
}
