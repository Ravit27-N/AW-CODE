package com.innovationandtrust.utils.commons;

import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonValidations {

  public static <T> void listNotEmpty(List<T> objects, String errorMessage) {
    if (objects.isEmpty()) {
      throw new EntityNotFoundException(errorMessage + " must not empty");
    }
  }

  public static <T> boolean listIsNotEmpty(List<T> objects) {
    return Objects.nonNull(objects) && !objects.isEmpty();
  }

  public static <T> boolean isNotEmptyString(T val) {
    return !val.equals("");
  }

  public static <T extends Date> boolean dateValid(T date) {
    return date != null && isNotEmptyString(date);
  }

  public static <T> boolean ok(T val) {
    return val != null && isNotEmptyString(val);
  }
}
