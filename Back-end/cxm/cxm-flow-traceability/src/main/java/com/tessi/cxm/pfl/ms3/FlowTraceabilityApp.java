package com.tessi.cxm.pfl.ms3;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.service.restclient.FileManagerFeignClient;
import com.tessi.cxm.pfl.ms3.service.restclient.HubDigitalFlowFeignClient;
import com.tessi.cxm.pfl.ms3.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms3.service.restclient.ServiceGatewayFeignClient;
import com.tessi.cxm.pfl.shared.loadbalancer.RibbonConfiguration;
import com.tessi.cxm.pfl.shared.model.FileProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakAdminClientProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakServiceImpl;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import java.util.Locale;
import javax.persistence.EntityManagerFactory;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import th.co.geniustree.springdata.jpa.repository.support.JpaSpecificationExecutorWithProjectionImpl;

@EnableBatchProcessing
@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@EnableFeignClients(clients = {
    ProfileFeignClient.class,
    FileManagerFeignClient.class,
    ProcessControlFeignClient.class,
    HubDigitalFlowFeignClient.class,
    ServiceGatewayFeignClient.class,
    SettingFeignClient.class
})
@RibbonClient(
    value = FeignClientConstants.CXM_FLOW_TRACEABILITY,
    configuration = RibbonConfiguration.class)
@EnableConfigurationProperties(KeycloakAdminClientProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(repositoryBaseClass = JpaSpecificationExecutorWithProjectionImpl.class)
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "${scheduler.lockAtMostFor:1m}", defaultLockAtLeastFor = "${scheduler.lockAtLeastFor:1m}")
public class FlowTraceabilityApp {

  public static void main(String[] args) {
    SpringApplication.run(FlowTraceabilityApp.class, args);
  }

  @Bean
  public FileService fileService(FileProperties fileProperties) {
    return new FileServiceImpl(fileProperties);
  }

  /**
   * Initialize bean of {@link ObjectMapper} to accept single string value as array.
   *
   * @return {@link ObjectMapper}
   */
  @Bean
  public ObjectMapper mapper() {
    var mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
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
  public KeycloakService keycloakService(@Autowired KeycloakAdminClientProperties props) {
    return new KeycloakServiceImpl(props);
  }

  @Bean
  public FileProperties fileProperties() {
    return new FileProperties();
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setDefaultLocale(new Locale("fr"));
    messageSource.setBasename("i18n/message");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

}
