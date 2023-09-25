package com.tessi.cxm.services.discovery;

import java.util.List;
import org.springframework.cloud.client.ServiceInstance;

/**
 * Generic service for service discovery.
 *
 * @author Sakal TUM.
 */
public interface GenericDiscoveryService {

  /**
   * Get all registered instances from service discovery.
   *
   * @return List of registered {@code ServiceInstance}.
   */
  List<ServiceInstance> getAllInstances();
}
