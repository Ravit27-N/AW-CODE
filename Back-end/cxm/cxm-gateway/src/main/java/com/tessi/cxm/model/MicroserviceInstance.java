package com.tessi.cxm.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class MicroserviceInstance implements Serializable {

  private String id;
  private String version;
  private String description;

  public MicroserviceInstance(String id, String version, String description) {
    this.id = id;
    this.version = version;
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MicroserviceInstance)) {
      return false;
    }
    MicroserviceInstance that = (MicroserviceInstance) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
