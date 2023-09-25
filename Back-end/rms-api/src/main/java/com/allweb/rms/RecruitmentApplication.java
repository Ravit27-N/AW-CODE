package com.allweb.rms;

import com.allweb.rms.config.DatasourceConfig;
import com.allweb.shared.service.keycloak.KeycloakAdminClientProperties;
import com.allweb.shared.service.keycloak.KeycloakService;
import com.allweb.shared.service.keycloak.KeycloakServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(
    exclude = {
      ReactiveElasticsearchRepositoriesAutoConfiguration.class,
      ReactiveElasticsearchRepositoriesAutoConfiguration.class,
      DataSourceAutoConfiguration.class
    })
@Import(DatasourceConfig.class)
@LoadBalancerClient(name = "aw-rms-service")
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableConfigurationProperties({KeycloakAdminClientProperties.class})
public class RecruitmentApplication {

  public static void main(String[] args) {
    SpringApplication.run(RecruitmentApplication.class, args);
  }

  @LoadBalanced
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public KeycloakService keycloakService(
      @Autowired KeycloakAdminClientProperties keycloakAdminClientProperties) {
    return new KeycloakServiceImp(keycloakAdminClientProperties);
  }
}
