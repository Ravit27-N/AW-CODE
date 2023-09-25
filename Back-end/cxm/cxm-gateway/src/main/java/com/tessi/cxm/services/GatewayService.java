package com.tessi.cxm.services;

import com.tessi.cxm.model.MicroserviceInfo;
import com.tessi.cxm.model.MicroserviceInstance;
import com.tessi.cxm.services.discovery.GenericDiscoveryService;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

/**
 * Handle gateway service info.
 *
 * @author Pisey CHORN.
 * @since 09-05-2022.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayService {

  private final GenericDiscoveryService genericDiscoveryService;

  /**
   * Used to get microservice info.
   *
   * @return list of {@link MicroserviceInfo}.
   */
  public List<MicroserviceInfo> getMicroserviceInfo() {
    return this.genericDiscoveryService.getAllInstances().stream()
        .map(this.getMicroserviceInfoMapper())
        .sorted(
            Comparator.comparing(MicroserviceInfo::getServiceName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  /**
   * Get {@code Function} utility which map from {@code ServiceInstance} to {@code
   * MicroserviceInfo}.
   *
   * @return {@code Function} which map from {@code ServiceInstance} to {@code MicroserviceInfo}
   *     instance.
   */
  private Function<ServiceInstance, MicroserviceInfo> getMicroserviceInfoMapper() {
    return serviceInstance -> {
      MicroserviceInfo microserviceInfo = new MicroserviceInfo();

      microserviceInfo.setServiceName(serviceInstance.getServiceId().toUpperCase(Locale.ROOT));
      microserviceInfo
          .getInstances()
          .add(
              new MicroserviceInstance(
                  serviceInstance.getInstanceId(),
                  serviceInstance.getMetadata().get("version"),
                  serviceInstance.getMetadata().get("description")));

      return microserviceInfo;
    };
  }
}
