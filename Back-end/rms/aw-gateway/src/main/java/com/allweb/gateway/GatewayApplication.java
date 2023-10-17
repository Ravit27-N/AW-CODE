package com.allweb.gateway;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

  @Bean
  public Caffeine<Object, Object> caffeineConfig() {
    return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES);
  }

  @Bean
  public CacheManager cacheManager(final Caffeine<Object, Object> caffeine) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }

  @Bean
  public KeyResolver authUserKeyResolver() {
    return exchange -> ReactiveSecurityContextHolder.getContext()
        .map(ctx -> ctx.getAuthentication().getPrincipal().toString());
  }
}
