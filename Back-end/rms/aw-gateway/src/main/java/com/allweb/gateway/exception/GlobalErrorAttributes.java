package com.allweb.gateway.exception;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {
    Throwable error = this.getError(request);
    log.error("Error occured", error);

    Map<String, Object> map = super.getErrorAttributes(request, options);
    map.put("errorDescription", "errorDescription");
    map.put("title", "title");
    map.put("tabTitle", "tabtitle");
    map.put("errorDescriptionTitle", "A error has occured! :(");
    return map;
  }
}
