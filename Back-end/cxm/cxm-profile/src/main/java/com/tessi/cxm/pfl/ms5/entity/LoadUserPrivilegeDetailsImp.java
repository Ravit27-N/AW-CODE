package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserPrivilegeDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadUserPrivilegeDetailsImp implements LoadUserPrivilegeDetails {
  private Long id;
  private String username;
}
