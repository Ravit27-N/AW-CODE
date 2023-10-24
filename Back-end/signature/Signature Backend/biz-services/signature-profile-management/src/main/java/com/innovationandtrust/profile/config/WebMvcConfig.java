package com.innovationandtrust.profile.config;

import com.innovationandtrust.configuration.datasource.CommonDatasourceConfiguration;
import com.innovationandtrust.configuration.security.SecurityProperty;
import com.innovationandtrust.configuration.sms.SMSProperty;
import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import com.innovationandtrust.profile.model.dto.SuperAdminDto;
import com.innovationandtrust.profile.service.SuperAdminUserService;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import feign.codec.ErrorDecoder;
import java.util.Set;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(
    value = {
      ApiNGProperty.class,
      SMSProperty.class,
      FeignClientProperty.class,
      SuperAdminProperty.class,
      FrontEndProperty.class
    })
public class WebMvcConfig extends CommonWebMvcConfigurer
    implements ApplicationListener<ApplicationReadyEvent> {

  private final SuperAdminUserService superAdminUserService;
  private final SecurityProperty securityProperty;
  private final SuperAdminProperty adminProperty;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] origins = securityProperty.getOrigins().toArray(String[]::new);
    registry.addMapping("/companies/**");
    registry
        .addMapping("/auth/**")
        .allowedOrigins(origins)
        .allowedMethods("GET", "PUT", "POST", "DELETE");
  }

  @Bean
  public CommonDatasourceConfiguration configureDatasource(final DataSource dataSource) {
    return new CommonDatasourceConfiguration(dataSource);
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    this.superAdminUserService.initSystemRoles(
        Set.of(RoleConstant.SUPER_ADMIN, RoleConstant.CORPORATE_ADMIN, RoleConstant.NORMAL_USER));
    if (!this.superAdminUserService.isUserExist(adminProperty.getEmail())) {
      var superAdmin = new SuperAdminDto();
      superAdmin.setFirstName(adminProperty.getFirstName());
      superAdmin.setLastName(adminProperty.getLastName());
      superAdmin.setEmail(adminProperty.getEmail());
      superAdmin.setPassword(adminProperty.getPassword());
      this.superAdminUserService.createSuperAdminUser(superAdmin);
    }
  }

  @Bean
  @RequestScope
  public FacadeUrlConfig facadeUriConfig() {
    return new FacadeUrlConfig();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }
}
