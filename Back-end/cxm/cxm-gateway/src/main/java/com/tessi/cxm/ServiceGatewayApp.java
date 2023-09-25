package com.tessi.cxm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import com.tessi.cxm.pfl.shared.discovery.config.DiscoveryConstants;
import com.tessi.cxm.pfl.shared.discovery.config.GenericDiscoveryProperties;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.services.discovery.ConsulDiscoveryService;
import com.tessi.cxm.services.discovery.EurekaDiscoveryService;
import com.tessi.cxm.services.discovery.GenericDiscoveryService;
import com.tessi.cxm.utils.GatewayUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerResilience4JFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@Import(ServiceDiscoveryAutoConfigurationImportSelector.class)
@EnableConfigurationProperties
public class ServiceGatewayApp {

  public static void main(String[] args) {
    SpringApplication.run(ServiceGatewayApp.class, args);
  }

  /*@Bean
  public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(1, 2);
  }

  @Bean
  public KeyResolver userKeyResolver() {
    return exchange -> Mono.just("1");
  }*/

  @Bean
  public SpringCloudCircuitBreakerFilterFactory resilience4jCircuitBreakerFactory(
      ReactiveResilience4JCircuitBreakerFactory reactiveCircuitBreakerFactory,
      ObjectProvider<DispatcherHandler> dispatcherHandlers) {
    return new SpringCloudCircuitBreakerResilience4JFilterFactory(
        reactiveCircuitBreakerFactory, dispatcherHandlers);
  }

  @Bean
  public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4jCircuitBreakerFactory(
      CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    var circuitBreakerFactory =
        new ReactiveResilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry);
    circuitBreakerFactory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
    circuitBreakerFactory.configure(
        builder ->
            builder
                .timeLimiterConfig(
                    TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(60)).build())
                .build(),
        GatewayUtils.GATEWAY_CIRCUIT_BREAKER);
    return circuitBreakerFactory;
  }

  @Bean
  RouterFunction<ServerResponse> routerFunction() {
    return RouterFunctions.route(
        RequestPredicates.GET("/"),
        req -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build());
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  @ConditionalOnProperty(value = DiscoveryConstants.DISCOVERY_TYPE_KEY, havingValue = DiscoveryConstants.EUREKA_DISCOVERY)
  public GenericDiscoveryService eurekaDiscoveryService(
      GenericDiscoveryProperties genericDiscoveryProperties,
      ObjectProvider<EurekaClient> eurekaClientObjectProvider) {
    return new EurekaDiscoveryService(genericDiscoveryProperties,
        eurekaClientObjectProvider.getIfAvailable());
  }

  @Bean
  @ConditionalOnProperty(value = DiscoveryConstants.DISCOVERY_TYPE_KEY, havingValue = DiscoveryConstants.CONSUL_DISCOVERY)
  public GenericDiscoveryService consulDiscoveryService(
      GenericDiscoveryProperties genericDiscoveryProperties,
      ObjectProvider<ConsulDiscoveryClient> consulDiscoveryClientObjectProvider) {
    return new ConsulDiscoveryService(genericDiscoveryProperties,
        consulDiscoveryClientObjectProvider.getIfAvailable());
  }

}
