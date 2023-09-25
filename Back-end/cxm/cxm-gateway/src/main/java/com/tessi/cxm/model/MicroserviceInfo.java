package com.tessi.cxm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class MicroserviceInfo implements Serializable, Comparable<MicroserviceInfo> {

  private String serviceName;
  private Set<MicroserviceInstance> instances = new HashSet<>();

  public MicroserviceInfo(MicroserviceInfoAdapter infoAdapter, String instanceId) {
    this.serviceName = infoAdapter.getApplicationName();
    this.instances.add(
        new MicroserviceInstance(
            instanceId, infoAdapter.getVersion(), infoAdapter.getDescription()));
  }

  public MicroserviceInfo getInstance(Instance instance) {
    this.serviceName = instance.getApp();
    this.instances.add(
        new MicroserviceInstance(
            instance.getInstanceId(),
            instance.getMetadata().getVersion(),
            instance.getMetadata().getDescription()));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(MicroserviceInfo o) {
    return this.serviceName.compareTo(o.serviceName);
  }
}
