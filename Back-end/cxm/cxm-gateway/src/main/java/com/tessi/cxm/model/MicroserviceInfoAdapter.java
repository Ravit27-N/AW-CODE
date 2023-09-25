package com.tessi.cxm.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class MicroserviceInfoAdapter implements Serializable {

  private String applicationName;
  private String version;
  private String description;
}
