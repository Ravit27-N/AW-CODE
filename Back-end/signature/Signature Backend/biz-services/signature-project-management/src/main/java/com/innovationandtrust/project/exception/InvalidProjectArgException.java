package com.innovationandtrust.project.exception;

import com.innovationandtrust.project.constant.ProjectBadRequestConstant;
import com.innovationandtrust.utils.exception.config.HandledException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Exception handler for invalid template. */
@Getter
@HandledException(status = HttpStatus.BAD_REQUEST, message = "Invalid project ")
public class InvalidProjectArgException extends RuntimeException {
  private String key = ProjectBadRequestConstant.DEFAULT;

  public InvalidProjectArgException(String message) {
    super(message);
  }

  public InvalidProjectArgException(String message, String key) {
    super(message);
    this.key = key;
  }
}
