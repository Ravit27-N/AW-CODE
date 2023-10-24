package com.innovationandtrust.utils.signatureidentityverification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class VerificationRequest {
  private MultipartFile documentBack;

  @NotNull(message = "documentFront is mandatory")
  private MultipartFile documentFront;

  @NotNull(message = "documentCountry is mandatory")
  private String documentCountry;

  @NotNull(message = "documentType is mandatory")
  private String documentType;

  @NotNull(message = "documentRotation is mandatory")
  private String documentRotation;
}
