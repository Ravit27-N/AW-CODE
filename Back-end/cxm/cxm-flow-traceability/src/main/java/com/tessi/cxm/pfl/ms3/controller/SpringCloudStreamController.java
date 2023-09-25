package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.service.ConsumerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flow Traceability", description = "Manage Flow Traceability")
@RestController
@RequestMapping("/v1/flow-traceability")
@RequiredArgsConstructor
@Slf4j
public class SpringCloudStreamController {
  private final StreamBridge streamBridge;

  @GetMapping("/producer/{value}")
  public ResponseEntity<Boolean> producer(@PathVariable String value) {
    log.info("Sending value {} to topic", value);
    return ResponseEntity.ok(streamBridge.send(ConsumerService.SIMPLE_TOPIC, value));
  }
}
