package com.tessi.cxm.pfl.ms32.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ProductionDataExporters {
  private static final Map<String, ProductionDataExporter> INSTANCES = new HashMap<>();

  public static void initInstances(List<ProductionDataExporter> instances) {
    INSTANCES.putAll(instances.stream()
        .collect(
            Collectors.toMap(
                ProductionDataExporter::getKey,
                productionDataExporter -> productionDataExporter)));
  }

  public static ProductionDataExporter getByKey(String key) {
    return INSTANCES.get(key);
  }
}
