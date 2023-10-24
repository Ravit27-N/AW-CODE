package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.CompleteSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.DocumentProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.GenerateOTPExecutionManager;
import com.innovationandtrust.process.chain.execution.SetupIndividualSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.SignatureFileExecutionManager;
import com.innovationandtrust.process.chain.execution.SigningInfoExecutionManager;
import com.innovationandtrust.process.chain.execution.UploadModifiedDocumentExecutionHandler;
import com.innovationandtrust.process.chain.execution.VerifyDocumentExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.SigningProcessExecutionManager;
import com.innovationandtrust.process.constant.DocumentProcessAction;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.OtpProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.SignatureFileConstant;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.FileAction;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Participant.ValidPhone;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SigningProcessingService {

  private final GenerateOTPExecutionManager otpExecutionManager;

  private final SigningProcessExecutionManager signingProcessExecutionManager;

  private final SigningInfoExecutionManager signingInfoExecutionManager;

  private final CompleteSignProcessExecutionManager completeSignProcessExecutionManager;

  private final UploadModifiedDocumentExecutionHandler uploadModifiedDocumentExecutionHandler;

  private final DocumentProcessExecutionManager documentProcessExecutionManager;

  private final SetupIndividualSignProcessExecutionManager individualSignProcessExecutionManager;

  private final ImpersonateTokenService impersonateTokenService;

  private final VerifyDocumentExecutionManager verifyDocumentExecutionManager;

  private final SignatureFileExecutionManager signatureFileExecutionManager;

  /**
   * To retrieve information on the project to be signed.
   *
   * @param flowId refers to the identity of {@link Project}
   * @param uuid refers to the uuid of {@link
   *     com.innovationandtrust.share.model.project.Participant}
   * @return the object of {@link SignInfo}
   */
  public SignInfo getSignInfo(String flowId, String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.signingInfoExecutionManager.execute(context);
    return context.get(SignProcessConstant.SIGNING_INFO, SignInfo.class);
  }

  public SignInfo getSignInfoExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    return this.getSignInfo(param.getFlowId(), param.getUuid());
  }

  /**
   * To validate phone number before generate OTP code;
   *
   * @param flowId refers to identity of {@link Project}
   * @param uuid refers to uuid of {@link Participant}
   * @param phoneNumber refers to phone number have to validate
   * @return object of {@link ValidPhone}
   */
  public ValidPhone validatePhoneNumber(String flowId, String uuid, String phoneNumber) {
    if (!StringUtils.hasText(phoneNumber)) {
      throw new IllegalArgumentException(
          "Invalid phone number! The phone number cannot be null or empty!");
    }
    var ctx = ProcessControlUtils.getProject(flowId, uuid);
    ctx.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    ctx.put(SignProcessConstant.PHONE_NUMBER, phoneNumber);
    ctx.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE_PHONE_NUMBER);
    this.otpExecutionManager.execute(ctx);
    return ctx
        .get(SignProcessConstant.PROJECT_KEY, Project.class)
        .getParticipantByUuid(uuid)
        .stream()
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Failed to validate the phone number!"))
        .getValidPhone();
  }

  public ValidPhone validatePhoneNumberExternal(
      String companyUuid, String phoneNumber, String token) {
    var param = this.getParams(companyUuid, token);
    return validatePhoneNumber(param.getFlowId(), param.getUuid(), phoneNumber);
  }

  public VerificationDocumentResponse validateDocument(
      String flowId, String uuid, DocumentVerificationRequest request) {
    var ctx = ProcessControlUtils.getProject(flowId, uuid);
    ctx.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    ctx.put(SignProcessConstant.DOCUMENTS_TO_VERIFY, request);

    this.verifyDocumentExecutionManager.execute(ctx);

    return ctx.get(
        SignProcessConstant.VERIFY_DOCUMENT_RESPONSE, VerificationDocumentResponse.class);
  }

  public VerificationDocumentResponse validateDocumentExternal(
      String companyUuid, String token, DocumentVerificationRequest request) {
    var param = this.getParams(companyUuid, token);
    return validateDocument(param.getFlowId(), param.getUuid(), request);
  }

  /**
   * To generate OTP code before signing process.
   *
   * @param flowId refers to the identity of {@link Project}
   * @param uuid refers to the uuid of {@link
   *     com.innovationandtrust.share.model.project.Participant}
   */
  public void generateOtp(String flowId, String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.GENERATE);
    otpExecutionManager.execute(context);
  }

  public void generateOtpExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    this.generateOtp(param.getFlowId(), param.getUuid());
  }

  /**
   * To generate and response OTP code before signing process(for testing environment only).
   *
   * @param flowId refers to the identity of {@link Project}
   * @param uuid refers to the uuid of {@link
   *     com.innovationandtrust.share.model.project.Participant}
   */
  public Optional<String> generateAndResponseOtp(String flowId, String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.GENERATE);
    otpExecutionManager.execute(context);
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    return project.getParticipantByUuid(uuid).map(Participant::getOtpCode);
  }

  /**
   * To validate OTP code before the signing process.
   *
   * @param flowId refers to the identity of {@link Project}
   * @param uuid refers to the uuid of {@link
   *     com.innovationandtrust.share.model.project.Participant}
   * @param otpCode refers to the generated code sent to a user via SMS or Email during the
   *     generation
   * @see SigningProcessingService#generateOtp(String, String)
   * @return OtpResponse, isValidated true if valid, nor false
   */
  public boolean validateOtp(String flowId, String uuid, String otpCode) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.OTP_VALUE, otpCode);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);
    this.otpExecutionManager.execute(context);
    return context
        .get(SignProcessConstant.PROJECT_KEY, Project.class)
        .getParticipantByUuid(uuid)
        .stream()
        .anyMatch(p -> p.getOtp().isValidated());
  }

  public boolean validateOtpExternal(String companyUuid, String token, String otpCode) {
    var param = this.getParams(companyUuid, token);
    return this.validateOtp(param.getFlowId(), param.getUuid(), otpCode);
  }

  /**
   * Handling the process of signing the documents.
   *
   * @param flowId refers to the identity of {@link Project}
   * @param uuid refers to the uuid of {@link
   *     com.innovationandtrust.share.model.project.Participant}
   */
  public void signDocuments(String flowId, String uuid) {
    this.signingProcessExecutionManager.execute(ProcessControlUtils.getProject(flowId, uuid));
  }

  public void uploadSignatureFile(
      String flowId, String uuid, MultipartFile file, SignatureMode mode) {
    if (mode.equals(SignatureMode.WRITE)) {
      throw new InvalidRequestException(
          "Signature mode must be " + SignatureMode.IMPORT + " or " + SignatureMode.DESIGN);
    }

    if (Objects.isNull(file)) {
      throw new InvalidRequestException("Signature file cannot be null.");
    }

    FileUtils.validateImageType(file, SignatureFileConstant.getFileExtensions());

    FileUtils.validateFileSize(file, SignatureFileConstant.MAX_SIZE);

    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.SIGNATURE_IMAGE, file);
    context.put(SignProcessConstant.FILE_ACTION, FileAction.UPLOAD);
    context.put(SignProcessConstant.SIGNATURE_MODE, mode);
    this.signatureFileExecutionManager.execute(context);
  }

  public void uploadSignatureFileExternal(
      String companyUuid, String token, SignatureMode mode, MultipartFile file) {
    var param = this.getParams(companyUuid, token);
    this.uploadSignatureFile(param.getFlowId(), param.getUuid(), file, mode);
  }

  public String viewSignatureFile(String flowId, String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.FILE_ACTION, FileAction.DOWNLOAD);
    this.signatureFileExecutionManager.execute(context);
    return context.get(SignProcessConstant.SIGNATURE_IMAGE, String.class);
  }

  public String viewSignatureFileExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    return this.viewSignatureFile(param.getFlowId(), param.getUuid());
  }

  public void removeSignatureFile(String flowId, String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.FILE_ACTION, FileAction.REMOVE);
    this.signatureFileExecutionManager.execute(context);
  }

  public void removeSignatureFileExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    this.removeSignatureFile(param.getFlowId(), param.getUuid());
  }

  public void signDocumentsExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    this.signingProcessExecutionManager.execute(
        ProcessControlUtils.getProject(param.getFlowId(), param.getUuid()));
  }

  public FileResponse downloadSignedDocument(String flowId, String docId) {
    var ctx = new ExecutionContext();
    ctx.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    ctx.put(SignProcessConstant.DOC_ID, docId);
    ctx.put(SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_SIGNED_DOC);
    this.completeSignProcessExecutionManager.execute(ctx);
    return ctx.get(SignProcessConstant.DOWNLOAD_SIGNED_DOC, FileResponse.class);
  }

  public FileResponse downloadSignedDocumentExternal(
      String companyUuid, String token, String docId) {
    var param = this.getParams(companyUuid, token);
    return this.downloadSignedDocument(param.getFlowId(), docId);
  }

  public String viewDocument(String flowId, String docId) {
    var ctx = new ExecutionContext();
    ctx.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    ctx.put(SignProcessConstant.DOC_ID, docId);
    ctx.put(SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.VIEW_DOC_FOR_SIGN);
    this.documentProcessExecutionManager.execute(ctx);
    return ctx.get(SignProcessConstant.VIEW_DOC_FOR_SIGN, String.class);
  }

  public String viewDocumentExternal(String companyUuid, String docId, String token) {
    var param = this.getParams(companyUuid, token);
    return viewDocument(param.getFlowId(), docId);
  }

  public void uploadModifiedDocument(MultipartFile file, String flowId, String docId) {
    var ctx = new ExecutionContext();
    ctx.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    ctx.put(SignProcessConstant.DOC_ID, docId);
    ctx.put(SignProcessConstant.UPLOAD_MODIFIED_DOC_FOR_SIGN, file);
    ctx.put(SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.UPLOAD_DOC_FOR_SIGN);
    ctx.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.uploadModifiedDocumentExecutionHandler.execute(ctx);
  }

  public void uploadModifiedDocumentExternal(
      String companyUuid, MultipartFile file, String docId, String token) {
    var param = this.getParams(companyUuid, token);
    this.uploadModifiedDocument(file, param.getFlowId(), docId);
  }

  public void setupIndividualSignProcess(String flowId, String uuid) {
    this.individualProcess(flowId, uuid);
  }

  public void setupIndividualSignProcessExternal(String companyUuid, String token) {
    var param = this.getParams(companyUuid, token);
    this.individualProcess(param.getFlowId(), param.getUuid());
  }

  public Optional<String> setupIndividualProcessOTPValue(String flowId, String uuid) {
    var context = this.individualProcess(flowId, uuid);
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    return project.getParticipantByUuid(uuid).map(Participant::getOtpCode);
  }

  private ExecutionContext individualProcess(String flowId, String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.GENERATE);
    this.individualSignProcessExecutionManager.execute(context);
    return context;
  }

  private TokenParam getParams(String companyUuid, String token) {
    return this.impersonateTokenService.validateImpersonateToken(companyUuid, token);
  }
}
