package com.tessi.cxm.services.discovery;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.tessi.cxm.pfl.shared.discovery.config.GenericDiscoveryProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;

/**
 * Eureka service discovery implementation on {@code GenericDiscoveryService}.
 *
 * @author Sakal TUM
 */
public class EurekaDiscoveryService extends BaseDiscoveryService {

  private final EurekaClient eurekaClient;

  /**
   * Create new {@code EurekaDiscoveryService}.
   *
   * @param genericDiscoveryProperties Generic discovery properties.
   * @param eurekaClient {@code EurekaClient}.
   */
  public EurekaDiscoveryService(
      GenericDiscoveryProperties genericDiscoveryProperties, EurekaClient eurekaClient) {
    super(genericDiscoveryProperties);
    this.eurekaClient = eurekaClient;
  }

  /** {@inheritDoc} */
  @Override
  public List<ServiceInstance> getAllInstances() {
    if (this.eurekaClient == null) {
      return Collections.emptyList();
    }
    var instances = new ArrayList<ServiceInstance>();

    List<Application> registeredApplications =
        this.eurekaClient.getApplications().getRegisteredApplications();

    registeredApplications.forEach(
        application -> {
          List<InstanceInfo> eurekaInstances = application.getInstances();
          if (!eurekaInstances.isEmpty()) {
            InstanceInfo instanceInfo = eurekaInstances.get(0);
            instances.add(new EurekaServiceInstance(instanceInfo));
          }
        });

    return instances;
  }
}
