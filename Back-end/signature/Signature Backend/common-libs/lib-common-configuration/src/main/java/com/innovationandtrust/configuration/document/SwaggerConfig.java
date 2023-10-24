package com.innovationandtrust.configuration.document;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.OAUTH2;
import static java.lang.String.valueOf;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.innovationandtrust.configuration.exception.ResponseErrorHandler;
import com.innovationandtrust.configuration.security.SecurityProperty;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
public class SwaggerConfig implements WebMvcConfigurer {
  private static final String REDIRECT_URL = "/swagger-ui.html";
  private final SecurityProperty keycloakProperty;
  private final SwaggerProperty swaggerProperty;

  @Value("${spring.mvc.servlet.path}")
  private String baseUrl;

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addRedirectViewController("/", baseUrl.concat(REDIRECT_URL));
    // use setStatusCode(HttpStatus.XYZ) for any custom status code if required, e.g.
    // MOVED_PERMANENTLY
    registry.addRedirectViewController("/swagger-ui", baseUrl.concat(REDIRECT_URL));
    // any other alias
    registry.addRedirectViewController("/api", baseUrl.concat(REDIRECT_URL));
  }

  @Bean
  protected OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .description(swaggerProperty.getDescription())
                .title(swaggerProperty.getTitle())
                .version(swaggerProperty.getVersion())
                .termsOfService(swaggerProperty.getTermOfService())
                .contact(
                    new Contact()
                        .email(swaggerProperty.getEmail())
                        .url(swaggerProperty.getUrl())
                        .name(swaggerProperty.getName())))
        .components(
            new Components()
                .addSecuritySchemes(
                    swaggerProperty.getSecurityKey(),
                    new SecurityScheme()
                        .type(OAUTH2)
                        .in(HEADER)
                        .flows(new OAuthFlows().password(new OAuthFlow().tokenUrl(tokenUrl())))))
        .addSecurityItem(new SecurityRequirement().addList(swaggerProperty.getSecurityKey()));
  }

  protected GroupedOpenApi groupedOpenApi(String group, String ... pathToMatch) {
    return GroupedOpenApi.builder()
        .group(group)
        .pathsToMatch(pathToMatch)
        .addOperationCustomizer(operationCustomizer())
        .build();
  }

  private OperationCustomizer operationCustomizer() {
    return (operation, method) ->
        operation.responses(
            new ApiResponses()
                .addApiResponse(
                    valueOf(OK.value()), operation.getResponses().get(valueOf(OK.value())))
                .addApiResponse(valueOf(BAD_REQUEST.value()), buildApiResponse(BAD_REQUEST))
                .addApiResponse(valueOf(UNAUTHORIZED.value()), buildApiResponse(UNAUTHORIZED))
                .addApiResponse(valueOf(FORBIDDEN.value()), buildApiResponse(FORBIDDEN))
                .addApiResponse(valueOf(NOT_FOUND.value()), buildApiResponse(NOT_FOUND))
                .addApiResponse(
                    valueOf(INTERNAL_SERVER_ERROR.value()), buildApiResponse(INTERNAL_SERVER_ERROR))
                .addApiResponse(valueOf(NOT_ACCEPTABLE.value()), buildApiResponse(NOT_ACCEPTABLE)));
  }

  private ApiResponse buildApiResponse(HttpStatus status) {
    return new ApiResponse()
        .description(status.getReasonPhrase())
        .content(
            new Content()
                .addMediaType(
                    APPLICATION_JSON_VALUE,
                    new io.swagger.v3.oas.models.media.MediaType().schema(buildSchema(status))));
  }

  private Schema<?> buildSchema(HttpStatus status) {
    var errorHandler =
        new ResponseErrorHandler(
            status, status.getReasonPhrase().concat(" occurred!"), new RuntimeException());
    var schema = new Schema<>();
    schema.setTitle("ResponseErrorHandler");
    schema.addProperty("status", new StringSchema().example(errorHandler.getStatus()));
    schema.addProperty("statusCode", new IntegerSchema().example(errorHandler.getStatusCode()));
    schema.addProperty("timestamp", new DateSchema().example(errorHandler.getTimestamp()));
    schema.addProperty("message", new StringSchema().example(errorHandler.getMessage()));
    schema.addProperty("debugMessage", new StringSchema().example(errorHandler.getDebugMessage()));
    schema.addProperty("subErrors", new ArraySchema().example(errorHandler.getSubErrors()));
    return schema;
  }

  private String tokenUrl() {
    return keycloakProperty
        .getKeycloakBaseUrl()
        .concat("/realms/")
        .concat(keycloakProperty.getKeycloakRealm())
        .concat("/protocol/openid-connect/token");
  }
}
