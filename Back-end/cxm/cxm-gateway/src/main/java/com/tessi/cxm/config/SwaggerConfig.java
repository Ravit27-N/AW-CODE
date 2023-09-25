package com.tessi.cxm.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class SwaggerConfig {
  private static final String OPEN_API_PATH_KEY = "paths";
  private static final String OPEN_API_DOC_ENDPOINT = "/api/v3/api-docs";

  private final ObjectMapper objectMapper;
  private final MicroserviceContextPathConfigurationProperties microserviceContextPathConfigurationProperties;

  @Value("${cxm.gateway.server.uri}")
  private String serverUri;

  public SwaggerConfig(
      ObjectMapper objectMapper,
      MicroserviceContextPathConfigurationProperties
          microserviceContextPathConfigurationProperties) {
    this.objectMapper = objectMapper;
    this.microserviceContextPathConfigurationProperties =
        microserviceContextPathConfigurationProperties;
  }

  @Bean
  public RouteLocator openApiSpecFilter(RouteLocatorBuilder routeLocatorBuilder) {
    return routeLocatorBuilder
        .routes()
        .route(
            "openapi",
            predicateSpec ->
                predicateSpec
                    .path(OPEN_API_DOC_ENDPOINT + "/**")
                    .filters(
                        gatewayFilterSpec ->
                            gatewayFilterSpec
                                .rewritePath(
                                    OPEN_API_DOC_ENDPOINT + "/(?<path>.*)",
                                    "/$\\{path}" + OPEN_API_DOC_ENDPOINT)
                                .modifyResponseBody(
                                    String.class,
                                    String.class,
                                    this
                                        ::filterConfiguredMicroserviceContextPathOnOpenApiEndpointPaths))
                    .uri(this.serverUri))
        .build();
  }

  private Mono<String> filterConfiguredMicroserviceContextPathOnOpenApiEndpointPaths(
      ServerWebExchange serverWebExchange, String jsonResponse) {
    return Mono.create(
        openApiJsonSink -> {
          var baseContextPath = this.getConfiguredMicroserviceContextPath(serverWebExchange);
          if (jsonResponse == null) {
            openApiJsonSink.success("");
            return;
          }
          try {
            var openApiJson = (ObjectNode) this.objectMapper.readTree(jsonResponse);
            var pathNode = (ObjectNode) openApiJson.get(OPEN_API_PATH_KEY);
            var pathMap =
                this.objectMapper.convertValue(
                    openApiJson.path(OPEN_API_PATH_KEY),
                    new TypeReference<Map<String, Object>>() {});

            pathMap.forEach(
                (field, pathJsonValue) -> {
                  pathNode.remove(field);
                  pathNode.set(
                      baseContextPath + field, this.objectMapper.valueToTree(pathJsonValue));
                });
            openApiJsonSink.success(openApiJson.set(OPEN_API_PATH_KEY, pathNode).toString());
          } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            openApiJsonSink.error(e);
          }
        });
  }

  private String getConfiguredMicroserviceContextPath(ServerWebExchange serverWebExchange) {
    var serverUriString = serverWebExchange.getRequest().getURI().toString();
    var serverName = serverUriString.replace(this.serverUri + OPEN_API_DOC_ENDPOINT + "/", "");
    if (serverName.contains("/")) {
      serverName = serverName.substring(0, serverName.indexOf("/"));
    }
    final String microserviceName = serverName;
    Optional<MicroserviceContextPathConfigurationProperties.MicroserviceDetails>
        microserviceContextPath =
            this.microserviceContextPathConfigurationProperties.getServices().stream()
                .filter(
                    microserviceDetails -> microserviceDetails.getName().equals(microserviceName))
                .findFirst();
    if (microserviceContextPath.isPresent()) {
      return microserviceContextPath.get().getContextPath();
    }
    return "";
  }
}
