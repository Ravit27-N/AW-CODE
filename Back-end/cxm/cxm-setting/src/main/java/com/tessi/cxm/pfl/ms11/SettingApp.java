package com.tessi.cxm.pfl.ms11;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tessi.cxm.pfl.ms11.config.Go2pdfFileConfig;
import com.tessi.cxm.pfl.ms11.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms11.entity.projection.DepositValidationProjection;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.loadbalancer.RibbonConfiguration;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;
import javax.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RefreshScope
@EnableDiscoveryClient
@Import(value = {ServiceDiscoveryAutoConfigurationImportSelector.class})
@EnableFeignClients(clients = {FileManagerResource.class, ProfileFeignClient.class,
    HubDigitalFlow.class})
@SpringBootApplication(exclude = QuartzAutoConfiguration.class)
@EnableConfigurationProperties({LocalFileConfig.class, Go2pdfFileConfig.class})
@RibbonClient(value = FeignClientConstants.CXM_SETTING, configuration = RibbonConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class SettingApp {

  public static void main(String[] args) {
    SpringApplication.run(SettingApp.class, args);
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
    return mapper;
  }

  @Bean
  public ModelMapper modelMapper() {
    var mapper = new ModelMapper();
    mapper.addMappings(
        new PropertyMap<DepositValidationProjection, DepositValidation>() {
          @Override
          protected void configure() {
            map().getContent().setFlowType(source.getFlowType());
            map().getContent().setIdCreator(source.getIdCreator());
            map().setScanActivation(source.isScanActivation());
            map().setConfigurationActivation(source.isConfigurationActivation());
          }
        });

    return mapper;
  }

  /**
   * The bean used to identity of transactionManager.
   *
   * @param entityManagerFactory refer to
   * @return {@link JpaTransactionManager}
   */
  @Primary // use as default
  @Bean(name = "transactionManager")
  public JpaTransactionManager transactionManagerPostgres(
      EntityManagerFactory entityManagerFactory) {
    var transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }

  @Bean
  public FileService fileService(LocalFileConfig localFileConfig) {
    return new FileServiceImpl(localFileConfig.getPath(), "");
  }

  @Bean
  public HubDigitalFlowHelper getHubDigitalFlowHelper(
      @Autowired ProfileFeignClient profileFeignClient,
      @Autowired HubDigitalFlow hubDigitalFlow) {
    return new HubDigitalFlowHelper(profileFeignClient, hubDigitalFlow);
  }

  @Bean
  public AESHelper aesHelper() {
    return new AESHelper();
  }
}
