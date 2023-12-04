package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This class handle on validate and verify document participant submitted. Available on advanced
 * signature level
 */
@Slf4j
@Component
public class VerificationProcessHandler extends AbstractExecutionHandler {

  private final SignatureIdentityVerificationFeignClient verificationFeignClient;

  public VerificationProcessHandler(
      SignatureIdentityVerificationFeignClient verificationFeignClient) {
    this.verificationFeignClient = verificationFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    var signatureLevel = project.getSignatureLevel();
    log.info("Project created in {} signature level...", signatureLevel);
    if (Objects.equals(signatureLevel, SignatureSettingLevel.ADVANCE.getValue())) {
      this.validateDocuments(context, project);
      context.put(SignProcessConstant.PROJECT_KEY, project);
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      return ExecutionState.NEXT;
    }

    throw new InvalidRequestException("Project is not advanced project...");
  }

  private void validateDocuments(ExecutionContext context, Project project) {
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    var participant = ProcessControlUtils.getParticipant(project, uuid);
    validate(participant);
    this.checkDossier(participant);
    var documents =
        context.get(SignProcessConstant.DOCUMENTS_TO_VERIFY, DocumentVerificationRequest.class);

    var result = verify(participant.getDossierId(), documents);
    participant.setDocumentVerified(result.isAuthenticity());
    context.put(SignProcessConstant.VERIFY_DOCUMENT_RESPONSE, result);
  }

  private static void validate(Participant participant) {
    var validPhone = participant.getValidPhone();
    if (!validPhone.isValid()) {
      throw new InvalidRequestException("Participant must validate phone...");
    }

    if (participant.isDocumentVerified()) {
      throw new InvalidRequestException("Participant document already verified...");
    }
  }

  private void checkDossier(Participant participant) {
    if (!StringUtils.hasText(participant.getDossierId())) {
      this.createDossier(participant);
    }
  }

  private VerificationDocumentResponse verify(
      String dossierId, DocumentVerificationRequest documents) {
    return this.verificationFeignClient.verifyDocument(
        dossierId,
        VerificationRequest.builder()
            .documentFront(documents.getDocumentFront())
            .documentBack(documents.getDocumentBack())
            .documentCountry(documents.getDocumentCountry().getValue())
            .documentType(documents.getDocumentType().getValue())
            .documentRotation(documents.getDocumentRotation().toString())
            .build());
  }

  private void createDossier(Participant participant) {
    var dossier =
        DossierDto.builder()
            .dossierName(participant.getFullName())
            .firstname(participant.getFirstName())
            .tel(participant.getPhone())
            .participantUuid(participant.getUuid())
            .verificationChoice(VerificationChoice.CLASSIC_DOCUMENT)
            .build();
    log.info("Creating dossier: {}", dossier.toString());
    this.verificationFeignClient.createDossier(dossier);
  }
}
