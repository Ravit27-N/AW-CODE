package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.NOT_FOUND)
public class TemplateNotFoundException extends RuntimeException {
  public TemplateNotFoundException(Long id) {
    super("Unable to load template id: " + id);
  }
}
