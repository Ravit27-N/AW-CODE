package com.innovationandtrust.utils.feignclient;

import feign.RequestTemplate;

public interface FacadeUrl {

  /** Get company uuid in current request. */
  String getUuid();

  /** Get company uuid in current request. */
  String getUserUuid();

  /**
   * Set the user uuid if the current request, does not have user uuid.
   *
   * @param userUuid the user uuid in current request.
   */
  void setUserUuid(String userUuid);

  /**
   * Replace the value of this function when need to request with facade url.
   *
   * @param requestTemplate contain url, context
   */
  void facadeRequest(RequestTemplate requestTemplate);
}
