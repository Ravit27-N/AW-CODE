package com.tessi.cxm.pfl.ms5.util;

import com.tessi.cxm.pfl.ms5.dto.UserOrganization;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchUserOrganization {
  private final Map<String, Map<String, Map<String, UserOrganization>>> organizationMap;

  public BatchUserOrganization(List<UserOrganization> userOrganizations) {
    this.organizationMap = this.groupOrgByAttributesMap(userOrganizations);
  }

  private Map<String, Map<String, Map<String, UserOrganization>>> groupOrgByAttributesMap(
      List<UserOrganization> userOrganizations) {
    return userOrganizations.stream()
        .collect(
            Collectors.groupingBy(
                client -> client.getClientName().toLowerCase(),
                Collectors.groupingBy(
                    division -> division.getDivisionName().toLowerCase(),
                    Collectors.toMap(
                        service -> service.getServiceName().toLowerCase(), Function.identity()))));
  }

  public UserOrganization getValidOrgInfo(
      String clientName, String divisionName, String serviceName) {
    UserOrganization userOrg = null;
    var clientMap = this.organizationMap.get(clientName.toLowerCase());
    if (clientMap != null) {
      var divisionMap = clientMap.get(divisionName.toLowerCase());
      if (divisionMap != null) {
        var serviceMap = divisionMap.get(serviceName.toLowerCase());
        if (serviceMap != null) {
          userOrg = serviceMap;
        }
      }
    }
    return userOrg;
  }
}
