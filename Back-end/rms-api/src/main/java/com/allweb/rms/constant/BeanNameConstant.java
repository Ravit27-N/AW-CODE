package com.allweb.rms.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanNameConstant {

  // Primary datasource
  public static final String PRIMARY_DATA_SOURCE_PROPERTY = "primaryDataSourceProperty";
  public static final String PRIMARY_DATA_SOURCE = "primaryDataSource";

  // Quartz datasource
  public static final String QUARTZ_DATA_SOURCE_PROPERTY = "quartzDataSourceProperty";
  public static final String QUARTZ_DATA_SOURCE = "quartzDataSource";
}
