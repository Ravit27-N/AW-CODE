package com.tessi.cxm.services.discovery;

import com.tessi.cxm.pfl.shared.discovery.config.GenericDiscoveryProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;

/**
 * Consul service discovery implementation on {@code GenericDiscoveryService}.
 *
 * @author Sakal TUM
 */
public class ConsulDiscoveryService extends BaseDiscoveryService {

  private final ConsulDiscoveryClient consulDiscoveryClient;
  private final List<String> serviceIdsToExclude = List.of("consul");

  /**
   * Create new {@code ConsulDiscoveryService}.
   *
   * @param genericDiscoveryProperties Generic discovery properties.
   * @param consulDiscoveryClient {@code ConsulDiscoveryClient}.
   */
  public ConsulDiscoveryService(
      GenericDiscoveryProperties genericDiscoveryProperties,
      ConsulDiscoveryClient consulDiscoveryClient) {
    super(genericDiscoveryProperties);
    this.consulDiscoveryClient = consulDiscoveryClient;
  }

  /** {@inheritDoc} */
  @Override
  public List<ServiceInstance> getAllInstances() {
    if (this.consulDiscoveryClient == null) {
      return Collections.emptyList();
    }
    var instances = new ArrayList<ServiceInstance>();

    // Group by serviceId
    var instanceGroupByServiceId =
        this.consulDiscoveryClient.getAllInstances().stream()
            .collect(Collectors.groupingBy(ServiceInstance::getServiceId));

    instanceGroupByServiceId.forEach(
        (serviceId, serviceInstances) -> {

          // Filter out external services
          if (!serviceInstances.isEmpty() && !serviceIdsToExclude.contains(serviceId)) {
            instances.add(serviceInstances.get(0));
          }
        });

    return instances;
  }
}
