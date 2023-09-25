package com.tessi.cxm.pfl.ms32;

import com.tessi.cxm.pfl.ms32.config.CSVExportingProperties;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.loadbalancer.RibbonConfiguration;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakAdminClientProperties;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import th.co.geniustree.springdata.jpa.repository.support.JpaSpecificationExecutorWithProjectionImpl;

@SpringBootApplication(exclude = {QuartzAutoConfiguration.class, DataSourceAutoConfiguration.class})
@RefreshScope
@EnableDiscoveryClient
@Import(value = {ServiceDiscoveryAutoConfigurationImportSelector.class})
@EnableConfigurationProperties({KeycloakAdminClientProperties.class, CSVExportingProperties.class})
@RibbonClient(value = FeignClientConstants.CXM_ANALYTICS, configuration = RibbonConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
    repositoryBaseClass = JpaSpecificationExecutorWithProjectionImpl.class,
    basePackages = "com.tessi.cxm.pfl.*.repository")
@EnableRetry
@EnableFeignClients(clients = {ProfileFeignClient.class, SettingFeignClient.class})
public class AnalyticsApp {

  public static void main(String[] args) {
    SpringApplication.run(AnalyticsApp.class, args);
  }
}
