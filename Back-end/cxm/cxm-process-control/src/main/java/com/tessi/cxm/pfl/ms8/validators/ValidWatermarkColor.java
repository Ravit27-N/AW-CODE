package com.tessi.cxm.pfl.ms8.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WatermarkColorValidator.class)
public @interface ValidWatermarkColor {
  String message() default "Invalid watermark color";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
