package com.allweb.rms.service.elastic;

public interface ElasticRequest<T> {
  RequestInfo getRequestInfo();

  T getArgument();
}
