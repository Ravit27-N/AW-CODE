package com.tessi.cxm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Instance {

  private String instanceId;
  private String hostName;
  private String app;
  private String ipAddr;
  private String status;
  private String overriddenStatus;
  private MetaData metadata;
  private String countryId;
  private String homePageUrl;
  private String statusPageUrl;
  private String healthCheckUrl;
  private String vipAddress;
  private String secureVipAddress;
  private String isCoordinatingDiscoveryServer;
  private Date lastUpdatedTimestamp;
  private Date lastDirtyTimestamp;
  private String actionType;
}
