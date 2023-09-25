package com.tessi.cxm.pfl.ms8.validators;

import com.tessi.cxm.pfl.ms8.constant.WatermarkPosition;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/** Implementation class for validate watermark position. */
public class WatermarkPositionValidator
    implements ConstraintValidator<ValidWatermarkPosition, String> {
  /**
   * @param constraintAnnotation annotation instance for a given constraint declaration
   */
  @Override
  public void initialize(ValidWatermarkPosition constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  /**
   * @param value object to validate
   * @param context context in which the constraint is evaluated
   * @return true if present else false
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return WatermarkPosition.isPresent(value);
  }
}
