package com.allweb.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class ControllerEndPoint {
    // provide your endPoint that you want to access

  @GetMapping
  public Mono<String> welcome() {
    return Mono.just("hello");
  }
}
