package com.tessi.cxm.pfl.ms8;

import com.cxm.tessi.pfl.shared.flowtreatment.FlowFileControl;
import com.cxm.tessi.pfl.shared.flowtreatment.Processing;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tessi.cxm.pfl.ms8.config.ClientTokenExchangeProperties;
import com.tessi.cxm.pfl.ms8.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ResourceExecutionHandler;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.CompositionFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.FileManagerFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.ProcessingFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.SwitchFeignClient;
import com.tessi.cxm.pfl.shared.core.mail.MailHandlerService;
import com.tessi.cxm.pfl.shared.core.mail.handler.MailHandlerServiceImpl;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProcessing;
import com.tessi.cxm.pfl.shared.loadbalancer.RibbonConfiguration;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakAdminClientProperties;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakServiceImpl;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import com.tessi.cxm.pfl.shared.utils.UnloadingUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@EnableIntegration
@RefreshScope
@EnableDiscoveryClient
@Import(value = {ServiceDiscoveryAutoConfigurationImportSelector.class})
@EnableFeignClients(
    clients = {
      ProfileFeignClient.class,
      FileManagerFeignClient.class,
      CompositionFeignClient.class,
      FileCtrlMngtFeignClient.class,
      SwitchFeignClient.class,
      ProcessingFeignClient.class,
      SettingFeignClient.class
    })
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, QuartzAutoConfiguration.class})
@RibbonClient(
    value = FeignClientConstants.CXM_PROCESS_CONTROL,
    configuration = RibbonConfiguration.class)
@EnableConfigurationProperties({
  KeycloakAdminClientProperties.class,
  ClientTokenExchangeProperties.class,
  LocalFileConfig.class
})
@EnableAsync
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaAuditing
public class ProcessControlApp {

  private final SimpleDateFormat compositionDateFormat =
      new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  public static void main(String[] args) {
    SpringApplication.run(ProcessControlApp.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    var mapper = new ModelMapper();
    mapper.addMappings(
        new PropertyMap<DepositedFlowLaunchRequest, FlowFileControl>() {
          @Override
          protected void configure() {
            map().setUserId(String.valueOf(source.getIdCreator()));
            map().setUserService(source.getServiceName());
            map().setName(source.getFileName());
            map().getFlow().setType(source.getFlowType());
          }
        });
    mapper
        .createTypeMap(FileDocumentProcessing.class, Processing.class)
        .addMappings(
            mapping ->
                mapping
                    .using(
                        (Converter<String, Date>)
                            context -> {
                              Date resutl = null;
                              try {
                                if (StringUtils.isNotBlank(context.getSource())) {
                                  resutl = compositionDateFormat.parse(context.getSource());
                                }
                              } catch (ParseException parseException) {
                                throw new IllegalArgumentException(
                                    "Failed to parse create date of processing.");
                              }
                              return resutl;
                            })
                    .map(FileDocumentProcessing::getCreationDate, Processing::setCreationDate));

    UnloadingUtils.setModelMapper(mapper);
    return mapper;
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
    SimpleFilterProvider simpleFilterProvider =
        new SimpleFilterProvider()
            .setFailOnUnknownId(false)
            .setDefaultFilter(SimpleBeanPropertyFilter.serializeAll());
    mapper.setFilterProvider(simpleFilterProvider);
    return mapper;
  }

  @Bean
  public FileService fileService() {
    final String defaultBaseDir = System.getProperty("java.io.tmpdir");
    return new FileServiceImpl(defaultBaseDir, "");
  }

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
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
  public MailHandlerService configureEmailSender() {
    return new MailHandlerServiceImpl();
  }

  @Bean
  ResourceExecutionHandler resourceExecutionHandler(
      @Autowired ResourceFileRepository resourceFileRepository,
      @Autowired FileManagerResource fileManagerResource
  ) {
    return new ResourceExecutionHandler(resourceFileRepository, fileManagerResource);
  }
}
