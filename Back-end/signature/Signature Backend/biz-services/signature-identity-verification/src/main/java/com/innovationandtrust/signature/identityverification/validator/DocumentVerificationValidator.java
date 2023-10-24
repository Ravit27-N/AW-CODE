package com.innovationandtrust.signature.identityverification.validator;

import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentType;
import io.micrometer.common.lang.NonNullApi;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/** Validator for DocumentVerificationRequest. */
@NonNullApi
@Component("beforeCreateDocumentVerificationValidator")
public class DocumentVerificationValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return DocumentVerificationRequest.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if ((target instanceof DocumentVerificationRequest request)
        && !request.getDocumentType().equals(DocumentType.PASSPORT)
        && Objects.isNull(request.getDocumentBack())) {
      errors.rejectValue("documentBack", "documentBack.empty", "documentBack is mandatory");
    }
  }
}
