package com.innovationandtrust.process.model;

import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Class about support to upload and verify documents for signing multiple projects */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsVerificationRequest {
  private List<SigningProcessDto> projects;
  private DocumentVerificationRequest verificationRequest;
}
