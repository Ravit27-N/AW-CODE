package com.tessi.cxm.pfl.ms8.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessControlIdentityResponse implements Serializable {
  private String modelName;
  private String channel;
  private String subChannel;
}
