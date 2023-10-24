package com.innovationandtrust.utils.schedule.job;

import com.innovationandtrust.utils.schedule.exception.ErrorMessage;

public interface JobDataValidation {
  void validate(ErrorMessage errorMessage);
}
