package com.innovationandtrust.utils.signatureidentityverification.feignclient;

import com.innovationandtrust.utils.feignclient.FeignClientMultipartConfiguration;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import com.innovationandtrust.utils.signatureidentityverification.model.DossierIdResponse;

import java.util.List;
import java.util.logging.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "signature-identity-verification",
    url = "${signature.feign-client.clients.signature-identity-verification-url}",
    path = "${signature.feign-client.contexts.signature-identity-verification-context-path}",
    configuration = FeignClientMultipartConfiguration.class)
public interface SignatureIdentityVerificationFeignClient {
  Logger log = Logger.getLogger(SignatureIdentityVerificationFeignClient.class.getName());

  @PostMapping("/v1/dossier/create")
  DossierIdResponse createDossier(@RequestBody DossierDto dossierDto);

  @PostMapping("/v1/dossier/create/multi")
  List<DossierIdResponse> createDossiers(@RequestBody List<DossierDto> dossierDtoS);

  @PutMapping("/v1/dossier/confirm/{dossierId}")
  void confirmDossier(@PathVariable(name = "dossierId") String dossierId);

  @PostMapping(
      value = "/v1/dossier/{dossierId}/verify",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  VerificationDocumentResponse verifyDocument(
      @PathVariable(name = "dossierId") String dossierId,
      @ModelAttribute VerificationRequest request);

  @GetMapping("/v1/dossier/{dossierId}")
  DossierDto getDossierById(@PathVariable(name = "dossierId") String dossierId);

  @PutMapping("/v1/dossier/validate/{dossierId}")
  void validateDossier(@PathVariable(name = "dossierId") String dossierId);
}
