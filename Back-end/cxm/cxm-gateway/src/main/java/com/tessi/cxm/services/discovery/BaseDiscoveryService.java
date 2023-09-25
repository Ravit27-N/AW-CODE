package com.tessi.cxm.services.discovery;

import com.tessi.cxm.pfl.shared.discovery.config.GenericDiscoveryProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** @author Sakal TUM */
@Getter
@RequiredArgsConstructor
public abstract class BaseDiscoveryService implements GenericDiscoveryService {

  private final GenericDiscoveryProperties genericDiscoveryProperties;
}
