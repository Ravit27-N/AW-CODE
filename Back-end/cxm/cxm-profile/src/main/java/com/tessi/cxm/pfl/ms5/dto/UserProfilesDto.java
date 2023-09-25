package com.tessi.cxm.pfl.ms5.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserProfilesDto implements Serializable {

  private String userId;
  private String username;
  private ProfileDto profile;
}
