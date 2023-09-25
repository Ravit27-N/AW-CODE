package com.tessi.cxm.pfl.ms8.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FlowDocumentAddressNumberValidator.class)
public @interface ValidFlowDocumentAddressLineNumber {
  String message() default "Line address is duplicated.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
