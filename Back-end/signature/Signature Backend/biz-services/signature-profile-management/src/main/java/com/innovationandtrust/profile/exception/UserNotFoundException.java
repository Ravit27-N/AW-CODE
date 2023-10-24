package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Handle user not found exception. */
@HandledException(status = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException() {
    super("Unable to find the user");
  }

  public UserNotFoundException(Long id) {
    super("Unable to find the user with id: " + id + "!");
  }

  public UserNotFoundException(String id) {
    super("Unable to find the user with user entity id: " + id + "!");
  }
}
