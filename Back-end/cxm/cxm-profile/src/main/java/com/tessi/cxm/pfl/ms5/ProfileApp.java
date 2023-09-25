package com.tessi.cxm.pfl.ms5;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.config.HubAccountEncryptionProperties;
import com.tessi.cxm.pfl.ms5.service.restclient.CampaignFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.FlowFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.TemplateFeignClient;
import com.tessi.cxm.pfl.ms5.util.UserResetPasswordValidator;
import com.tessi.cxm.pfl.shared.config.EnableProxyProperties;
import com.tessi.cxm.pfl.shared.config.ResttemplateFactory;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.loadbalancer.RibbonConfiguration;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakAdminClientProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakServiceImpl;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import com.tessi.cxm.pfl.shared.utils.AddressValidator;
import java.time.Duration;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import th.co.geniustree.springdata.jpa.repository.support.JpaSpecificationExecutorWithProjectionImpl;

@RefreshScope
@EnableRetry
@EnableFeignClients(clients = {
    FileManagerResource.class,
    CampaignFeignClient.class,
    TemplateFeignClient.class,
    FlowFeignClient.class,
    ProcessControlFeignClient.class,
    HubDigitalFlow.class,
    SettingFeignClient.class,
    ProfileFeignClient.class
})
@EnableDiscoveryClient
@Import(value = {ServiceDiscoveryAutoConfigurationImportSelector.class})
@SpringBootApplication(exclude = {QuartzAutoConfiguration.class})
@RibbonClient(value = FeignClientConstants.CXM_PROFILE, configuration = RibbonConfiguration.class)
@EnableConfigurationProperties({KeycloakAdminClientProperties.class, HubAccountEncryptionProperties.class,
        EnableProxyProperties.class})
@EnableJpaRepositories(
    repositoryBaseClass = JpaSpecificationExecutorWithProjectionImpl.class,
    basePackages = "com.tessi.cxm.pfl.*.repository")
@EnableTransactionManagement(proxyTargetClass = true)
public class ProfileApp {

  public static void main(String[] args) {
    SpringApplication.run(ProfileApp.class, args);
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
    return mapper;
  }

  @Bean(UserResetPasswordValidator.DURATION_TOKEN_EXPIRED)
  public Duration validateDuration(
      @Value("${cxm.duration-of-token-expired}") String dateTokenExpired) {
    return UserResetPasswordValidator.durationParser(dateTokenExpired);
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
  public AESHelper aesHelper(@Value("${cxm.secret-key.aes-encryption}") String tokenSecret) {
    return new AESHelper(tokenSecret);
  }

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ResttemplateFactory resttemplateFactory(
      RestTemplate restTemplate, EnableProxyProperties proxyProperties) {
    return new ResttemplateFactory(restTemplate, proxyProperties);
  }

  @Bean
  public AddressValidator addressLineValidatorUtil(
      ResttemplateFactory resttemplateFactory) {
    return new AddressValidator(resttemplateFactory);
  }
}
