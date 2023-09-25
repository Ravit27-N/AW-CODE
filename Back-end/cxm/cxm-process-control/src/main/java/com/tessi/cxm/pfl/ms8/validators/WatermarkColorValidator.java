package com.tessi.cxm.pfl.ms8.validators;

import com.tessi.cxm.pfl.ms8.constant.WatermarkColor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/** Implementation class for validate watermark color. */
public class WatermarkColorValidator implements ConstraintValidator<ValidWatermarkColor, String> {
  /**
   * @param constraintAnnotation annotation instance for a given constraint declaration
   */
  @Override
  public void initialize(ValidWatermarkColor constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  /**
   * @param value object to validate
   * @param context context in which the constraint is evaluated
   * @return true if present else false
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return WatermarkColor.isPresent(value);
  }
}
