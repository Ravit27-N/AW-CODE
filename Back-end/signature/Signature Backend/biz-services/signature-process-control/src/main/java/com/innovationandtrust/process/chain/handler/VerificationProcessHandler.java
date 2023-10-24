package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationProcessHandler extends AbstractExecutionHandler {

  private final SignatureIdentityVerificationFeignClient verificationFeignClient;

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
    project
        .getParticipantByUuid(uuid)
        .ifPresentOrElse(
            participant -> {
              var validPhone = participant.getValidPhone();
              if (!validPhone.isValid()) {
                throw new InvalidRequestException("Participant must validate phone...");
              }

              if (participant.isDocumentVerified()) {
                throw new InvalidRequestException("Participant document already verified...");
              }

              var dossierId = participant.getDossierId();
              if (!StringUtils.hasText(participant.getDossierId())) {
                this.createDossier(participant);
              } else {
                // Verify dossier still available on database
                var dossier = this.verificationFeignClient.getDossierById(dossierId);
                if (Objects.isNull(dossier)) {
                  this.createDossier(participant);
                }
              }

              var documents =
                  context.get(
                      SignProcessConstant.DOCUMENTS_TO_VERIFY, DocumentVerificationRequest.class);

              var result =
                  this.verificationFeignClient.verifyDocument(
                      dossierId,
                      VerificationRequest.builder()
                          .documentFront(documents.getDocumentFront())
                          .documentBack(documents.getDocumentBack())
                          .documentCountry(documents.getDocumentCountry().getValue())
                          .documentType(documents.getDocumentType().getValue())
                          .documentRotation(documents.getDocumentRotation().toString())
                          .build());

              participant.setDocumentVerified(result.isAuthenticity());
              context.put(SignProcessConstant.VERIFY_DOCUMENT_RESPONSE, result);
            },
            () -> {
              throw new InvalidRequestException("Invalid participant uuid " + uuid);
            });
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
