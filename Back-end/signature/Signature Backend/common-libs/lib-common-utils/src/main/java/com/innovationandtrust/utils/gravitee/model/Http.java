package com.innovationandtrust.utils.gravitee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Http {
  private Boolean keepAlive;
  private Boolean followRedirects;
  private int readTimeout;
  private int idleTimeout;
  private int connectTimeout;
  private int maxConcurrentConnections;
  private Boolean pipelining;
  private Boolean useCompression;
}
