package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(Long id, String entityName) {
    super("Unable to find the " + entityName + " with id: " + id);
  }

  public EntityNotFoundException(String entityName, String param) {
    super("Unable to find the " + entityName + " with this: " + param);
  }

  public EntityNotFoundException(String message) {
    super(message);
  }

  public EntityNotFoundException(String token, Long id) {
    super("Invalid token " + token);
  }
}
