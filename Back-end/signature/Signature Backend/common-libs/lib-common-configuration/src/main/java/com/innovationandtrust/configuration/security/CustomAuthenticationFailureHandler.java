package com.innovationandtrust.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.configuration.exception.ResponseErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    var errorHandler = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED);
    errorHandler.setMessage(exception.getMessage());
    errorHandler.setTimestamp(Calendar.getInstance().getTime());
    try {
      response.getOutputStream().println(objectMapper.writeValueAsString(errorHandler));
    } catch (IOException ex) {
      log.error("Authentication failed", ex);
    }
  }
}
