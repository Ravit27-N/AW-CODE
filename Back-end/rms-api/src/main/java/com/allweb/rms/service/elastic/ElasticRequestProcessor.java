package com.allweb.rms.service.elastic;

public interface ElasticRequestProcessor<T> {
  void process(ElasticRequest<T> elasticRequest);
}
