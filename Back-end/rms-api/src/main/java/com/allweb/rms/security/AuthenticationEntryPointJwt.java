package com.allweb.rms.security;

import com.allweb.rms.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      AuthenticationException authenticationException)
      throws IOException {

    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    final ObjectMapper mapper = new ObjectMapper();
    ApiError error = new ApiError(HttpStatus.UNAUTHORIZED);
    error.setMessage("An Authentication object was not found in the SecurityContext");
    mapper.writeValue(httpServletResponse.getOutputStream(), error);
  }
}
