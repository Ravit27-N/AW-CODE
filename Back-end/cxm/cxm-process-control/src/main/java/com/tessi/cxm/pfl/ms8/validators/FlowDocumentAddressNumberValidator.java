package com.tessi.cxm.pfl.ms8.validators;

import com.tessi.cxm.pfl.ms8.dto.FlowDocumentAddressLineDto;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/** Implementation class for validate address line number. */
public class FlowDocumentAddressNumberValidator
    implements ConstraintValidator<
        ValidFlowDocumentAddressLineNumber, List<FlowDocumentAddressLineDto>> {
  /**
   * @param constraintAnnotation annotation instance for a given constraint declaration
   */
  @Override
  public void initialize(ValidFlowDocumentAddressLineNumber constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  /**
   * @param value object to validate
   * @param context context in which the constraint is evaluated
   * @return true if present else false
   */
  @Override
  public boolean isValid(List<FlowDocumentAddressLineDto> value, ConstraintValidatorContext context) {
    return value.stream().map(FlowDocumentAddressLineDto::getAddressLineNumber).distinct().count()
        == value.size();
  }
}
