package com.tessi.cxm.pfl.ms5.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO implements Serializable {
  private long id;
  private String username;
  private String serviceName;
  private String divisionName;
  private String clientName;
  private long serviceId;
  private long divisionId;
  private long clientId;
}
