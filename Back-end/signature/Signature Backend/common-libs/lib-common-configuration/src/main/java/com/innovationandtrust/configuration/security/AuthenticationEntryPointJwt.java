package com.innovationandtrust.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.configuration.exception.ResponseErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      AuthenticationException exception) {

    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    final ObjectMapper mapper = new ObjectMapper();
    var error = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED);
    error.setMessage(exception.getMessage());
    try {
      mapper.writeValue(httpServletResponse.getOutputStream(), error);
    } catch (IOException e) {
      log.error("Authentication failed", e);
      throw new AccessDeniedException(exception.getMessage());
    }
  }
}
