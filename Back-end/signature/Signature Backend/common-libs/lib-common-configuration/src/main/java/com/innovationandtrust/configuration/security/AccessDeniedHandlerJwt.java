package com.innovationandtrust.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.configuration.exception.ResponseErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AccessDeniedHandlerJwt implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      AccessDeniedException e)
      throws IOException {
    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

    var error = new ResponseErrorHandler(HttpStatus.FORBIDDEN);
    error.setMessage("You don't have required privileges to perform this action.");
    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(httpServletResponse.getOutputStream(), error);
  }
}
