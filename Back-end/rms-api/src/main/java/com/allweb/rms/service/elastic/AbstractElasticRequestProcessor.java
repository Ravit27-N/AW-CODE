package com.allweb.rms.service.elastic;

import lombok.Getter;

@Getter
public abstract class AbstractElasticRequestProcessor<T> implements ElasticRequestProcessor<T> {
  private final String key;

  protected AbstractElasticRequestProcessor(String key) {
    this.key = key;
  }

  @Override
  public void process(ElasticRequest<T> elasticRequest) {
    if (validate(elasticRequest)) {
      this.doProcess(elasticRequest);
    }
  }

  protected abstract void doProcess(ElasticRequest<T> elasticRequest);

  protected abstract boolean validate(ElasticRequest<T> elasticRequest);
}
