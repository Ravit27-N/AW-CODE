package com.tessi.cxm.pfl.ms15;

import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.loadbalancer.RibbonConfiguration;
import com.tessi.cxm.pfl.shared.model.FileProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakAdminClientProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakServiceImpl;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RefreshScope
@EnableDiscoveryClient
@Import(ServiceDiscoveryAutoConfigurationImportSelector.class)
@EnableFeignClients(clients = {Go2pdfResource.class, SettingFeignClient.class,
    FileManagerResource.class,
    HubDigitalFlow.class,
    ProfileFeignClient.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, QuartzAutoConfiguration.class})
@RibbonClient(
    value = FeignClientConstants.CXM_PROCESSING,
    configuration = RibbonConfiguration.class)
@EnableConfigurationProperties({KeycloakAdminClientProperties.class, FileProperties.class})
public class ProcessingApp {

  public static void main(String[] args) {
    SpringApplication.run(ProcessingApp.class, args);
  }

  @Bean
  public KeycloakService keycloakService(@Autowired KeycloakAdminClientProperties props) {
    return new KeycloakServiceImpl(props);
  }

  @Bean
  public ObjectMapper mapper() {
    var mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper
        .configOverride(String.class)
        .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

    SimpleFilterProvider simpleFilterProvider =
        new SimpleFilterProvider()
            .setFailOnUnknownId(false)
            .setDefaultFilter(SimpleBeanPropertyFilter.serializeAll());
    mapper.setFilterProvider(simpleFilterProvider);

    return mapper;
  }

  @Bean
  public FileService fileService(FileProperties fileProperties) {
    return new FileServiceImpl(fileProperties);
  }

  @Bean
  public ModelMapper getModelMapper(){
    return new ModelMapper();
  }

  @Bean
  public HubDigitalFlowHelper getHubDigitalFlowHelper(
      @Autowired ProfileFeignClient profileFeignClient,
      @Autowired HubDigitalFlow hubDigitalFlow,
      @Autowired KeycloakService keycloakService
  ) {
    return new HubDigitalFlowHelper(profileFeignClient, hubDigitalFlow, keycloakService);
  }
}
