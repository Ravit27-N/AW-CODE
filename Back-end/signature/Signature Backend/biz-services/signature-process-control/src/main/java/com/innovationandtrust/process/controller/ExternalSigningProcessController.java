package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.service.SigningProcessingService;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.Participant.ValidPhone;
import com.innovationandtrust.utils.eid.model.RequestSignViaSmsResponse;
import com.innovationandtrust.utils.eid.model.VideoIDAuthorizationDto;
import com.innovationandtrust.utils.eid.model.VideoIDVerificationDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
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
@RequestMapping("/")
public class ExternalSigningProcessController {

  private final SigningProcessingService signService;

  public ExternalSigningProcessController(SigningProcessingService signService) {
    this.signService = signService;
  }

  @Tag(name = "1. Sign info")
  @GetMapping("/sign-info/{companyUuid}")
  public ResponseEntity<SignInfo> findDocuments(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    return ResponseEntity.ok(this.signService.getSignInfoExternal(companyUuid, token));
  }

  @Tag(name = "2. View the document")
  @GetMapping("/documents/view/{companyUuid}")
  public ResponseEntity<String> loadPdfDocument(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("docId") String docId,
      @RequestParam("token") String token) {
    return ResponseEntity.ok(signService.viewDocumentExternal(companyUuid, docId, token));
  }

  @Tag(name = "3. Validate phone number")
  @PostMapping("/otp/validate/phone-number/{companyUuid}")
  public ResponseEntity<ValidPhone> validatePhoneNumber(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("phone") String phoneNumber,
      @RequestParam("token") String token) {
    return ResponseEntity.ok(
        this.signService.validatePhoneNumberExternal(companyUuid, phoneNumber, token));
  }

  @Tag(name = "4. Validate document")
  @PostMapping(
      value = "/validate/document/{companyUuid}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<VerificationDocumentResponse> validateDocument(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("token") String token,
      @Valid @ModelAttribute DocumentVerificationRequest request) {
    return ResponseEntity.ok(
        this.signService.validateDocumentExternal(companyUuid, token, request));
  }

  @Tag(name = "5. Generate OTP")
  @PostMapping("/otp/generate/{companyUuid}")
  public ResponseEntity<Void> generateOtp(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.signService.generateOtpExternal(companyUuid, token);
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
  @PostMapping("/otp/validate/{companyUuid}")
  public ResponseEntity<OtpInfo> validateOtp(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("otp") String opt,
      @RequestParam("token") String token) {
    return new ResponseEntity<>(
        this.signService.validateOtpExternal(companyUuid, token, opt), HttpStatus.OK);
  }

  @Tag(name = "8. Signing the document")
  @PostMapping("/sign/{companyUuid}")
  public ResponseEntity<Void> signDocuments(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.signService.signDocumentsExternal(companyUuid, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "9. Download document after signed")
  @GetMapping("/sign/download/{companyUuid}")
  public ResponseEntity<Resource> downloadSignedDocument(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("token") String token,
      @RequestParam("docId") String docId) {
    var response = this.signService.downloadSignedDocumentExternal(companyUuid, token, docId);
    return new ResponseEntity<>(
        new ByteArrayResource(response.getResource()), response.getResourceHeader(), HttpStatus.OK);
  }

  @Tag(name = "10. Upload document for signing after modified")
  @PostMapping(value = "/sign/upload/{companyUuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadModifiedDocument(
      @PathVariable("companyUuid") String companyUuid,
      @Valid @RequestParam("file") MultipartFile file,
      @RequestParam("docId") String docId,
      @RequestParam("token") String token) {
    this.signService.uploadModifiedDocumentExternal(companyUuid, file, docId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "11. Setup signing flow for individual")
  @PostMapping("/sign/individual/setup/{companyUuid}")
  public ResponseEntity<Void> setupIndividualProcess(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.signService.setupIndividualSignProcessExternal(companyUuid, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = "12. Get generate OTP value for individual (for testing environment only)")
  @PostMapping("/sign/individual/setup/{flowId}/value")
  public ResponseEntity<Optional<String>> setupIndividualProcessOtpValue(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(
        this.signService.setupIndividualProcessOTPValue(flowId, uuid), HttpStatus.OK);
  }

  @PostMapping(
      value = "/sign/upload/{companyUuid}/signature",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadSignatureFile(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("token") String token,
      @RequestParam("mode") SignatureMode mode,
      @Valid @RequestParam("file") MultipartFile file) {
    this.signService.uploadSignatureFileExternal(companyUuid, token, mode, file);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/sign/view/{companyUuid}/signature")
  public ResponseEntity<String> viewSignatureFile(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    return new ResponseEntity<>(
        this.signService.viewSignatureFileExternal(companyUuid, token), HttpStatus.OK);
  }

  @DeleteMapping("/sign/remove/{companyUuid}/signature")
  public ResponseEntity<Void> removeSignatureFile(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.signService.removeSignatureFileExternal(companyUuid, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/sign/authorization/{companyUuid}/video-id")
  public ResponseEntity<VideoIDAuthorizationDto> requestVideoIDAuthentication(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    return new ResponseEntity<>(
        this.signService.requestVideoIDAuthenticationExternal(companyUuid, token), HttpStatus.OK);
  }

  @PostMapping("/sign/verification/{companyUuid}/video-id")
  public ResponseEntity<VideoIDVerificationDto> requestVerificationVideoID(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("token") String token,
      @RequestParam("videoId") String videoId) {
    return new ResponseEntity<>(
        this.signService.requestVerificationVideoIDExternal(companyUuid, token, videoId),
        HttpStatus.OK);
  }

  @PostMapping("/sign/request-sign/{companyUuid}")
  public ResponseEntity<RequestSignViaSmsResponse> requestToSignDocument(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    return new ResponseEntity<>(
        this.signService.requestToSignDocumentsExternal(companyUuid, token), HttpStatus.OK);
  }

  @PostMapping("/sign/sign-document/{companyUuid}")
  public ResponseEntity<Boolean> signDocument(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("token") String token,
      @RequestParam("otpCode") String otpCode) {
    return new ResponseEntity<>(
        this.signService.signDocumentExternal(companyUuid, token, otpCode), HttpStatus.OK);
  }
}
