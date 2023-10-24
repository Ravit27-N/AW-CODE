package com.innovationandtrust.utils.exception.config;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String s, Response response) {
    var lineSepartor = System.lineSeparator();
    log.info("Response from feign client request "
            + lineSepartor + "+==[Method={}] "
            + lineSepartor + "+==[Status={}]"
            + lineSepartor + "+==[Reason={}]"
            + lineSepartor + "+==[Headers={}]", s, response.status(), response.reason(), response.headers());

    log.info("+++++++ Full response from feign client +++++++");
    log.info("{}", response);
    var status = HttpStatus.resolve(response.status());
    if (Objects.isNull(response.body())) {
      log.error("Response: {} {}", s, response);
      return new FeignClientRequestException(status, response);
    }
    try (InputStream bodyIs = response.body().asInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      String statusStr = "status";
      var exception =
          mapper.readValue(bodyIs, new TypeReference<Map<String, Map<String, Object>>>() {});
      log.info("Response body : [{}]", exception);
      log.error("Read exception: " + exception);

      if (exception.containsKey("e")) {
        if (!Objects.nonNull(exception.get(statusStr))) {
          var ex = exception.get("e");
          return new FeignClientRequestException(
              HttpStatus.resolve(Integer.parseInt(String.valueOf(ex.get(statusStr)))),
              String.valueOf(ex.get("name")));
        }

        return new FeignClientRequestException(
            HttpStatus.resolve(Integer.parseInt(String.valueOf(exception.get(statusStr)))),
            String.valueOf(exception.get("name")));
      }

      if (exception.containsKey("error")) {
        var ex = exception.get("error");
        return new FeignClientRequestException(
            HttpStatus.resolve(Integer.parseInt(String.valueOf(ex.get("statusCode")))),
            String.valueOf(ex.get("message")));
      }
      return new FeignClientRequestException(
          HttpStatus.resolve(Integer.parseInt(String.valueOf(exception.get("statusCode")))),
          String.valueOf(exception.get("message")));
    } catch (StreamReadException | DatabindException e) {
      log.error("Unable to convert feign exception body ", e);
      log.error("Original Feign client error message : {}", e.getOriginalMessage());
      return new FeignClientRequestException(status, e.getMessage(), e);
    } catch (IOException e) {
      log.error("Feign client failed" + e);
      return new FeignClientRequestException(status, e.getMessage(), e);
    }
  }


}
