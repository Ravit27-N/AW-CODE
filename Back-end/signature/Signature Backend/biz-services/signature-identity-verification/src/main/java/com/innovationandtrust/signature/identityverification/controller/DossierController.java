package com.innovationandtrust.signature.identityverification.controller;

import com.innovationandtrust.signature.identityverification.constant.dossier.DossierConstants;
import com.innovationandtrust.signature.identityverification.model.dto.dossier.DossierIdResponse;
import com.innovationandtrust.signature.identityverification.model.dto.dossier.DossierResponse;
import com.innovationandtrust.signature.identityverification.service.DossierService;
import com.innovationandtrust.signature.identityverification.validator.DocumentVerificationValidator;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for dossier. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/dossier")
public class DossierController {
  private final DossierService dossierService;
  private final DocumentVerificationValidator validator;

  @PostMapping(DossierConstants.Endpoint.CREATE_DOSSIER)
  @Tag(name = "Create dossier", description = "To create a dossier.")
  public ResponseEntity<DossierIdResponse> createDossier(@RequestBody DossierDto dossierDto) {
    return ResponseEntity.ok(dossierService.createDossier(dossierDto));
  }

  @PostMapping(DossierConstants.Endpoint.CREATE_DOSSIERS)
  @Tag(name = "Create dossiers", description = "To create multiple dossier.")
  public ResponseEntity<List<DossierIdResponse>> createDossier(
      @RequestBody List<DossierDto> dossierDtoS) {
    return ResponseEntity.ok(dossierService.createDossiers(dossierDtoS));
  }

  @GetMapping(DossierConstants.Endpoint.FIND_BY_DOSSIER_ID)
  @Tag(name = "Get dossier", description = "To get dossier.")
  public ResponseEntity<DossierResponse> getCaseIdentity(@PathVariable String dossierId) {
    return ResponseEntity.ok(this.dossierService.getDossierDto(dossierId));
  }

  @PatchMapping(DossierConstants.Endpoint.UPDATE_DOSSIER_UUID)
  @Tag(name = "Update dossier uuid", description = "To update dossier uuid.")
  public ResponseEntity<Void> updateDossierUuid(
      @RequestParam String dossierId, @RequestParam String uuid) {
    this.dossierService.updateDossierUuid(dossierId, uuid);
    return ResponseEntity.ok().build();
  }

  @PostMapping(
      value = DossierConstants.Endpoint.DOSSIER_VERIFY,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Tag(name = "Verify a document", description = "To verify a document.")
  public ResponseEntity<VerificationDocumentResponse> verifyDocument(
      @PathVariable String dossierId, @Valid @ModelAttribute DocumentVerificationRequest request) {
    return ResponseEntity.ok(this.dossierService.verifyDocument(dossierId, request));
  }

  @InitBinder("documentVerificationRequest")
  public void initBinder(WebDataBinder binder) {
    binder.addValidators(validator);
  }

  @PutMapping(DossierConstants.Endpoint.DOSSIER_CONFIRM)
  @Tag(name = "Confirm dossier", description = "To confirm a dossier.")
  public ResponseEntity<Void> confirmDossier(@PathVariable String dossierId) {
    this.dossierService.confirmDossier(dossierId);
    return ResponseEntity.ok().build();
  }

  @PutMapping(DossierConstants.Endpoint.DOSSIER_VALIDATE)
  @Tag(
      name = "Validate dossier",
      description = "To validate a dossier. Participant validated phone")
  public ResponseEntity<Void> validateDossier(@PathVariable String dossierId) {
    this.dossierService.validateDossier(dossierId);
    return ResponseEntity.ok().build();
  }
}
