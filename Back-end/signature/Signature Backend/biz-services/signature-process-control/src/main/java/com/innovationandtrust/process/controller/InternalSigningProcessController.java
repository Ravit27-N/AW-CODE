package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.service.SigningProcessingService;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.Participant.ValidPhone;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class InternalSigningProcessController {

  private final SigningProcessingService signService;

  @Tag(name = "1. Sign info")
  @GetMapping("/sign-info/{flowId}")
  public ResponseEntity<SignInfo> findDocuments(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return ResponseEntity.ok(this.signService.getSignInfo(flowId, uuid));
  }

  @Tag(name = "2. View the document")
  @GetMapping("/documents/view/{flowId}")
  public ResponseEntity<String> loadPdfDocument(
      @PathVariable("flowId") String flowId, @RequestParam("docId") String docId) {
    return ResponseEntity.ok(signService.viewDocument(flowId, docId));
  }

  @Tag(name = "3. Validate phone number")
  @PostMapping("/otp/validate/phone-number/{flowId}")
  public ResponseEntity<ValidPhone> validatePhoneNumber(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("phone") String phoneNumber) {
    return ResponseEntity.ok(this.signService.validatePhoneNumber(flowId, uuid, phoneNumber));
  }

  @Tag(name = "4. Validate document")
  @PostMapping(
      value = "/validate/document/{flowId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<VerificationDocumentResponse> validateDocument(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @Valid @ModelAttribute DocumentVerificationRequest request) {
    return ResponseEntity.ok(this.signService.validateDocument(flowId, uuid, request));
  }

  @Tag(name = "5. Generate OTP")
  @PostMapping("/otp/generate/{flowId}")
  public ResponseEntity<Void> generateOtp(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    this.signService.generateOtp(flowId, uuid);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "6. Generate OTP (for testing environment only)")
  @PostMapping("/otp/generate/{flowId}/value")
  public ResponseEntity<Optional<String>> generateAndResponseOtp(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(
        this.signService.generateAndResponseOtp(flowId, uuid), HttpStatus.OK);
  }

  @Tag(name = "7. Validate OTP")
  @PostMapping("/otp/validate/{flowId}")
  public ResponseEntity<Boolean> validateOtp(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("otp") String opt) {
    return new ResponseEntity<>(this.signService.validateOtp(flowId, uuid, opt), HttpStatus.OK);
  }

  @Tag(name = "8. Signing the document")
  @PostMapping("/sign/{flowId}")
  public ResponseEntity<Void> signDocuments(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    this.signService.signDocuments(flowId, uuid);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "9. Download document after signed")
  @GetMapping("/sign/download/{flowId}")
  public ResponseEntity<Resource> downloadSignedDocument(
      @PathVariable("flowId") String flowId, @RequestParam("docId") String docId) {
    var response = this.signService.downloadSignedDocument(flowId, docId);
    return new ResponseEntity<>(
        new ByteArrayResource(response.getResource()), response.getResourceHeader(), HttpStatus.OK);
  }

  @Tag(name = "10. Upload document for signing after modified")
  @PostMapping(value = "/sign/upload/{flowId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadModifiedDocument(
      @PathVariable("flowId") String flowId,
      @Valid @RequestParam("file") MultipartFile file,
      @RequestParam("docId") String docId) {
    this.signService.uploadModifiedDocument(file, flowId, docId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "11. Setup signing flow for individual")
  @PostMapping("/sign/individual/setup/{flowId}")
  public ResponseEntity<Void> setupIndividualProcess(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    this.signService.setupIndividualSignProcess(flowId, uuid);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "12. Get generate OTP value for individual (for testing environment only)")
  @PostMapping("/sign/individual/setup/{flowId}/value")
  public ResponseEntity<Optional<String>> setupIndividualProcessOtpValue(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(
        this.signService.setupIndividualProcessOTPValue(flowId, uuid), HttpStatus.OK);
  }

  @Tag(name = "13. Sign")
  @PostMapping(
      value = "/sign/upload/{flowId}/signature",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadSignatureFile(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("mode") SignatureMode mode,
      @Valid @RequestParam("file") MultipartFile file) {
    this.signService.uploadSignatureFile(flowId, uuid, file, mode);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/sign/view/{flowId}/signature")
  public ResponseEntity<String> viewSignatureFile(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(this.signService.viewSignatureFile(flowId, uuid), HttpStatus.OK);
  }

  @DeleteMapping("/sign/remove/{flowId}/signature")
  public ResponseEntity<Void> removeSignatureFile(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    this.signService.removeSignatureFile(flowId, uuid);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
