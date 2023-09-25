package com.tessi.cxm.pfl.ms32.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms32.service.ProductionDataExporter;
import com.tessi.cxm.pfl.ms32.service.ProductionDataExporters;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakAdminClientProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakServiceImpl;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** A global configuration class. Must not annotate with @RefreshScope. */
@Configuration
public class GlobalConfiguration {

  /**
   * Bean of {@link ObjectMapper} to accept single string value as array.
   *
   * @return {@link ObjectMapper}
   */
  @Bean
  public ObjectMapper mapper() {
    var mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    return mapper;
  }

  @Bean
  public KeycloakService keycloakService(@Autowired KeycloakAdminClientProperties props) {
    return new KeycloakServiceImpl(props);
  }

  @Autowired
  public void setPrivilegeValidationUtil(ProfileFeignClient profileFeignClient) {
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  @Autowired
  public void productionDataExporters(List<ProductionDataExporter> instances) {
    ProductionDataExporters.initInstances(instances);
  }
}
