package com.tessi.cxm.pfl.ms5.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileKey implements Serializable {
  private Long user;
  private Long profile;
}
