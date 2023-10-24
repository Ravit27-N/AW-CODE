package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import feign.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Slf4j
@HandledException(status = HttpStatus.INTERNAL_SERVER_ERROR)
public class FeignClientRequestException extends RuntimeException {

  private final HttpStatus status;

  public FeignClientRequestException() {
    super("Failed during request via feign client");
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public FeignClientRequestException(HttpStatus status) {
    super("Failed during request via feign client");
    this.status = status;
  }

  public FeignClientRequestException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public FeignClientRequestException(HttpStatus status, String message, Throwable ex) {
    super(message, ex);
    this.status = status;
  }

  public FeignClientRequestException(HttpStatus status, Response response) {
    super("Failed during request via feign client");
    this.status = status;
    log.error("Feign client response" + response);
  }
}
