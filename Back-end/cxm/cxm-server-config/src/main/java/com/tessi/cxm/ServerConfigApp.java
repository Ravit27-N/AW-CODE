package com.tessi.cxm;

import com.tessi.cxm.pfl.shared.discovery.config.GenericDiscoveryPropertiesResolver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ServerConfigApp {

  public static void main(String[] args) {
    new SpringApplicationBuilder(ServerConfigApp.class)
        .listeners(new GenericDiscoveryPropertiesResolver())
        .run(args);
  }
}

