package com.innovationandtrust.utils.signatureidentityverification.dto;

import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentCountry;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentRotationType;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentVerificationRequest {
  private MultipartFile documentBack;

  @NotNull(message = "documentFront is mandatory")
  private MultipartFile documentFront;

  @NotNull(message = "documentCountry is mandatory")
  private DocumentCountry documentCountry;

  @NotNull(message = "documentType is mandatory")
  private DocumentType documentType;

  @NotNull(message = "documentRotation is mandatory")
  private DocumentRotationType documentRotation;

  @SuppressWarnings("unused")
  public void setDocumentType(String documentType) {
    this.documentType = DocumentType.fromString(documentType);
  }

  @SuppressWarnings("unused")
  public void setDocumentRotation(int documentRotation) {
    this.documentRotation = DocumentRotationType.fromInt(documentRotation);
  }

  @SuppressWarnings("unused")
  public void setDocumentCountry(String documentCountry) {
    this.documentCountry = DocumentCountry.fromString(documentCountry);
  }
}
