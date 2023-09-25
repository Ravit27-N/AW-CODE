package com.tessi.cxm.pfl.ms8.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WatermarkPositionValidator.class)
public @interface ValidWatermarkPosition {
  String message() default "Invalid watermark position";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
