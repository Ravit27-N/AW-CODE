package com.innovationandtrust.utils.signatureidentityverification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerificationDocumentResponse {
    private String status;
    private String message;
    private String id;
    private boolean authenticity;
    private String trace;
}
