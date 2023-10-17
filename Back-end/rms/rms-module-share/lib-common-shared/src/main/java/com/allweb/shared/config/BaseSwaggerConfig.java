package com.allweb.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;

public abstract class BaseSwaggerConfig {

  private static final String OAUTH_SCHEME_NAME = "OAuth_security_schema";

  protected abstract String authServerUrl();

  protected abstract String realm();

  protected abstract SwaggerConfigProperties swaggerConfigProperties();

  private String getAuthServerURL() {
    if (authServerUrl() == null) {
      throw new NullPointerException("The authServerUrl not yet implement");
    }
    return authServerUrl();
  }

  private String getRealm() {
    if (realm() == null) {
      throw new NullPointerException("The realm not yet implement");
    }
    return realm();
  }

  private SwaggerConfigProperties getSwaggerConfigProperties() {
    if (swaggerConfigProperties() == null) {
      throw new NullPointerException("The swaggerConfigProperties not yet implement");
    }
    return swaggerConfigProperties();
  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .servers(
            List.of(new Server().url(this.getSwaggerConfigProperties().getUrl()))
        )
        .components(
            new Components()
                .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme())
        )
        .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
        .info(
            new Info()
                .title(this.getSwaggerConfigProperties().getTitle())
                .version(this.getSwaggerConfigProperties().getVersion())
                .description(this.getSwaggerConfigProperties().getDescription())
        );
  }

  private SecurityScheme createOAuthScheme() {
    OAuthFlows flows = createOAuthFlows();
    return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
        .flows(flows);
  }

  private OAuthFlows createOAuthFlows() {
    OAuthFlow flow = createAuthorizationCodeFlow();
    return new OAuthFlows().implicit(flow);
  }

  private OAuthFlow createAuthorizationCodeFlow() {
    return new OAuthFlow()
        .authorizationUrl(
            getAuthServerURL() + "/realms/" + getRealm() + "/protocol/openid-connect/auth")
        .tokenUrl(getAuthServerURL() + "/realms/" + getRealm() + "/protocol/openid-connect/token");
  }
}