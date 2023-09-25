package com.tessi.cxm.controller;

import com.tessi.cxm.services.GatewayService;
import com.tessi.cxm.model.MicroserviceInfo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class GatewayAppController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final GatewayService gatewayService;

  public GatewayAppController(GatewayService gatewayService) {
    this.gatewayService = gatewayService;
  }

  /**
   * Use to retrieve token.
   *
   * @param authorizedClient registration client of keycloak
   * @return token value as {@link String}
   */
  @GetMapping("/token")
  public Mono<String> getToken(
      @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
    return Mono.just(authorizedClient.getAccessToken().getTokenValue());
  }

  /**
   * Used to handle fallback.
   *
   * @param exchange ServerWebExchange
   * @return
   */
  @GetMapping("/fallback")
  public Mono<String> fallback(ServerWebExchange exchange) {
    Throwable exception =
        exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
    logger.debug("", exception);
    return Mono.just("fallback-gateway");
  }

  /**
   * Used to filter all microservice version.
   * @return list of {@link MicroserviceInfo}.
   */
  @GetMapping("/microservice-info")
  public ResponseEntity<List<MicroserviceInfo>> filterMicroserviceVersion(){
   return ResponseEntity.ok(this.gatewayService.getMicroserviceInfo());
  }

}
