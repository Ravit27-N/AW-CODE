package com.allweb.rms.service.elastic;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class BaseElasticRequest<T> implements ElasticRequest<T> {
  private final RequestInfo requestInfo = new RequestInfo();

  @Setter private T argument;

  protected BaseElasticRequest(String key) {
    this.requestInfo.setKey(key);
  }

  protected BaseElasticRequest(String key, T argument) {
    this.requestInfo.setKey(key);
    this.argument = argument;
  }
}
