package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.util.HubUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerServiceProvidersDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.ServiceProviderResponse;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HubService {

  private final HubDigitalFlow hubDigitalFlow;
  private final HubUtil hubUtil;

  public ServiceProviderResponse getServiceProvider(List<String> channel) {
    String hubToken = this.hubUtil.getHubTokenByUser(
        AuthenticationUtils.getPreferredUsername());
    return this.hubDigitalFlow.getServiceProvider(channel, hubToken);
  }

  public CustomerServiceProvidersDto getCustomerServiceProvider(String channel) {
    String hubToken = this.hubUtil.getHubTokenByUser(
        AuthenticationUtils.getPreferredUsername());
    return this.hubDigitalFlow.getCustomerServiceProvider(channel, hubToken);
  }

  public CustomerServiceProvidersDto saveCustomerServiceProvider(
      CustomerServiceProvidersDto customerServiceProviderDto) {
    String hubToken = this.hubUtil.getHubTokenByUser(
        AuthenticationUtils.getPreferredUsername());
    return this.hubDigitalFlow.saveCustomerServiceProvider(customerServiceProviderDto, hubToken);
  }


}
