package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.model.DocumentsVerificationRequest;
import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.model.SigningProcessDto;
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
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1")
public class InternalSigningProcessController {

  private static final String PROJECTS_GROUP = "Multiple projects sign process.";

  private final SigningProcessingService signService;

  public InternalSigningProcessController(SigningProcessingService signService) {
    this.signService = signService;
  }

  @Tag(name = "1. Sign info")
  @GetMapping("/sign-info/{flowId}")
  public ResponseEntity<SignInfo> findDocuments(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid) {
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
  public ResponseEntity<OtpInfo> validateOtp(
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

  @GetMapping("/sign/authorization/{flowId}/video-id")
  public ResponseEntity<VideoIDAuthorizationDto> requestVideoIDAuthentication(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(
        this.signService.requestVideoIDAuthentication(flowId, uuid), HttpStatus.OK);
  }

  @PostMapping("/sign/verification/{flowId}/video-id")
  public ResponseEntity<VideoIDVerificationDto> requestVerificationVideoID(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("videoId") String videoId) {
    return new ResponseEntity<>(
        this.signService.requestVerificationVideoID(flowId, uuid, videoId), HttpStatus.OK);
  }

  @PostMapping("/sign/request-sign/{flowId}")
  public ResponseEntity<RequestSignViaSmsResponse> requestToSignDocument(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(
        this.signService.requestToSignDocuments(flowId, uuid), HttpStatus.OK);
  }

  @PostMapping("/sign/sign-document/{flowId}")
  public ResponseEntity<Boolean> signDocument(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("otpCode") String otpCode) {
    return new ResponseEntity<>(
        this.signService.signDocument(flowId, uuid, otpCode), HttpStatus.OK);
  }

  /**
   * To sign multiple projects at the same time
   *
   * @param requests refers to {@link SigningProcessDto}
   */
  @Tag(name = PROJECTS_GROUP)
  @PostMapping("/projects/sign")
  public ResponseEntity<List<SigningProcessDto>> signMultipleProjects(
      @RequestBody List<SigningProcessDto> requests) {
    return new ResponseEntity<>(this.signService.signProjects(requests), HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @PostMapping("/projects/otp/validate/phone-number")
  public ResponseEntity<ValidPhone> validatePhoneOfProjects(
      @RequestBody List<SigningProcessDto> requests, @RequestParam("phone") String phone) {
    return new ResponseEntity<>(
        this.signService.validatePhoneNumber(requests, phone), HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @PostMapping("/projects/otp/generate")
  public ResponseEntity<Void> generateOptOfProjects(@RequestBody List<SigningProcessDto> requests) {
    this.signService.generateOtp(requests);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @PostMapping("/projects/otp/validate")
  public ResponseEntity<OtpInfo> validateOptOfProjects(
      @RequestBody List<SigningProcessDto> requests, @RequestParam("otp") String otpCode) {
    return new ResponseEntity<>(this.signService.validateOtp(requests, otpCode), HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @PostMapping(
      value = "/projects/sign/upload/signature-file",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadSignatureFile(
      @RequestPart(value = "file") MultipartFile file) {
    return new ResponseEntity<>(this.signService.uploadSignatureFile(file), HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @PostMapping(value = "/projects/sign/upload/signature")
  public ResponseEntity<Void> uploadSignatureFile(
      @RequestParam("fileName") String fileName,
      @RequestParam("mode") SignatureMode mode,
      @RequestBody List<SigningProcessDto> requests) {
    this.signService.uploadSignatureFile(requests, fileName, mode);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @DeleteMapping("/projects/sign/remove/signature")
  public ResponseEntity<Void> removeSignatureFile(@RequestBody List<SigningProcessDto> requests) {
    this.signService.removeSignatureFile(requests);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Tag(name = PROJECTS_GROUP)
  @PostMapping(
      value = "/projects/validate/documents",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<VerificationDocumentResponse> validateDocument(
      @Valid @ModelAttribute DocumentsVerificationRequest request) {
    return ResponseEntity.ok(this.signService.validateDocument(request));
  }
}
