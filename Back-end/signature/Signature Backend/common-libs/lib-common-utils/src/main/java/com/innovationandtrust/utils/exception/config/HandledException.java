package com.innovationandtrust.utils.exception.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpStatus;

/** Custom annotation for handled exceptions. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HandledException {

  /** Status to return. */
  HttpStatus status();

  String key() default "";

  /** Status code to return. */
  int statusCode() default 0;

  /** Message to log. */
  String message() default "Error occur...";
}
