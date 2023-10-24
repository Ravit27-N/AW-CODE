package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.CommonExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handled runtime exceptions. */
@Slf4j
@RestControllerAdvice
public class SignatureProfileExceptionHandler extends CommonExceptionHandler {}
