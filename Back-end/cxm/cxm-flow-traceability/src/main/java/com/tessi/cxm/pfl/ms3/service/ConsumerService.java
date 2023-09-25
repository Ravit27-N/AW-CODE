package com.tessi.cxm.pfl.ms3.service;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerService {
  public static final String SIMPLE_TOPIC = "cloud-stream";

  @Bean("simpleConsumer")
  public Consumer<String> onReceive() {

    return message -> log.info("Received the value {} in Consumer", message);
  }
}
