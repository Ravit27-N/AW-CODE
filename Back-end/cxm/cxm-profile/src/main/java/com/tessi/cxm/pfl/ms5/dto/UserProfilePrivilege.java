package com.tessi.cxm.pfl.ms5.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfilePrivilege implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private long id;
  private String displayName;
  private boolean isAdmin = false; // default
  List<UserProfileProjection> functionalities;
}
