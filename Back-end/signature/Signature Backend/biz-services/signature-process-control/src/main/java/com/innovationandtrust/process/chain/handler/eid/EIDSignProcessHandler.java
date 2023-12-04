package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.DocumentDetail;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.eid.EIDProperty;
import com.innovationandtrust.utils.eid.model.DocumentRequest;
import com.innovationandtrust.utils.eid.model.SignDocumentRequest;
import com.innovationandtrust.utils.eid.model.TextSetting;
import com.innovationandtrust.utils.eid.model.VisualParameter;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EIDSignProcessHandler extends AbstractExecutionHandler {

  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final ModelMapper modelMapper;
  private final ProjectFeignClient projectFeignClient;
  private final String timeZone;
  private final String dateFormat;

  public EIDSignProcessHandler(
      ApiNgFeignClientFacade apiNgFeignClient,
      @Autowired @NotNull EIDProperty eIDProperty,
      ModelMapper modelMapper,
      ProjectFeignClient projectFeignClient) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.timeZone = eIDProperty.getTimeZone();
    this.dateFormat = eIDProperty.getDateFormat();
    this.modelMapper = modelMapper;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {

    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    if (!Objects.equals(SignatureSettingLevel.QUALIFY.getValue(), project.getSignatureLevel())) {
      log.warn(
          "[EIDSignProcessHandler] Request to sign with EID is support only Qualify Signature.");
      return ExecutionState.END;
    }

    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    final String optCode = context.get(SignProcessConstant.OTP_CODE, String.class);

    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            (Participant participant) -> signDocument(context, project, optCode, participant));

    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.SIGNED_DOCUMENT);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);

    return ExecutionState.NEXT;
  }

  private void signDocument(
      ExecutionContext context, Project project, String optCode, Participant participant) {

    checkParticipantIsSigner(participant);
    this.checkErrorValidateOTP(participant);
    SignDocumentRequest request = this.createRequest(project, participant, optCode);

    try {
      this.apiNgFeignClient.signDocument(project.getSessionId(), request);
      this.updateProjectAfterSign(participant.getId(), project.getDocuments());
      participant.setSignedDate(Date.from(Instant.now()));
      participant.setSigned(true);
      context.put(SignProcessConstant.SIGN_DOCUMENT, true);
    } catch (RuntimeException e) {
      log.error("[EIDSignProcessHandler] error while signature document: {}", e.getMessage());
      final var lastErrorValidationOtp = participant.getErrorValidationOtp();
      participant.setErrorValidationOtp(lastErrorValidationOtp + 1);
      context.put(SignProcessConstant.SIGN_DOCUMENT, false);
    }
  }

  private SignDocumentRequest createRequest(
      Project project, Participant participant, String optCode) {

    final DocumentRequest[] documents = this.createDocumentRequest(project, participant.getId());
    return SignDocumentRequest.builder()
        .actor(participant.getActorUrl())
        .documents(documents)
        .otp(optCode)
        .tag(project.getTemplate().getSignProcess().getVal())
        .build();
  }

  private DocumentRequest[] createDocumentRequest(Project project, Long signatoryId) {

    return project.getDocuments().stream()
        .map((Document document) -> getDocumentRequest(signatoryId, document))
        .toArray(DocumentRequest[]::new);
  }

  private DocumentRequest getDocumentRequest(Long signatoryId, Document document) {
    var visualParameter = new VisualParameter();
    final var textSetting = new TextSetting(this.dateFormat, this.timeZone);

    if (Objects.isNull(document.getDetails()) || document.getDetails().isEmpty()) {
      log.warn("[EIDSignProcessHandler] Document details is empty.");
      visualParameter.visualParameterDefault();

      return DocumentRequest.builder()
          .documentUrl(document.getDocUrl())
          .visualParameters(new VisualParameter())
          .textSettings(textSetting)
          .build();
    }

    final DocumentDetail detail =
        document.getDetails().stream()
            .filter(
                documentDetail ->
                    documentDetail.getSignatoryId().equals(signatoryId)
                        && documentDetail.getDocumentId().equals(document.getId()))
            .findFirst()
            .orElse(null);
    visualParameter = this.createVisualParameterRequest(detail);

    return DocumentRequest.builder()
        .documentUrl(document.getDocUrl())
        .visualParameters(visualParameter)
        .textSettings(textSetting)
        .build();
  }

  private VisualParameter createVisualParameterRequest(DocumentDetail detail) {
    if (Objects.isNull(detail)) {
      final var visualParameter = new VisualParameter();
      visualParameter.visualParameterDefault();
      return visualParameter;
    }
    final VisualParameter request = this.modelMapper.map(detail, VisualParameter.class);
    request.setTextAlign(Double.parseDouble(detail.getTextAlign()));
    request.setPageNumber(detail.getPageNum());
    return request;
  }

  private void updateProjectAfterSign(Long participantId, List<Document> documents) {
    final var signatory = new SignatoryRequest(participantId, DocumentStatus.SIGNED);
    final var documentRequest =
        documents.stream()
            .map(com.innovationandtrust.share.model.project.DocumentRequest::new)
            .toList();
    this.projectFeignClient.updateProjectAfterSigned(
        new ProjectAfterSignRequest(signatory, documentRequest));
  }

  private static void checkParticipantIsSigner(Participant participant) {
    if (participant.isSigner() && participant.isSigned()) {
      throw new IllegalArgumentException(
          "[EIDSignProcessHandler] The documents are already signed and cannot be processed again!");
    }
  }

  private void checkErrorValidateOTP(Participant participant) {
    final var MAX_OTP_ERROR = 3;
    final var errorValidationOtp = participant.getErrorValidationOtp();
    if (errorValidationOtp >= MAX_OTP_ERROR) {
      log.warn("[RequestToSignHandler] OTP code failed 3 time. Cannot request sign.");
      throw new BadRequestException(
          "Cannot request sign (OTP validation has been tried many times).");
    }
  }
}
