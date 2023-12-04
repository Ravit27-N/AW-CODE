package com.innovationandtrust.process.chain.handler.sign;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.DocumentRequest;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.constant.ActorActionConstant;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.SignRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.InternalErrorException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SigningProcessHandler extends AbstractExecutionHandler {
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final ProjectFeignClient projectFeignClient;
  private final UpdateToProcessingHandler updateToProcessingHandler;

  public SigningProcessHandler(
      ApiNgFeignClientFacade apiNgFeignClient,
      ProjectFeignClient projectFeignClient,
      UpdateToProcessingHandler updateToProcessingHandler) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.projectFeignClient = projectFeignClient;
    this.updateToProcessingHandler = updateToProcessingHandler;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var participant = context.get(SignProcessConstant.PARTICIPANT, Participant.class);
    var signRequest = context.get(SignProcessConstant.API_NG_SIGN_REQUEST, SignRequest.class);

    if (Objects.isNull(signRequest) || Objects.isNull(participant)) {
      log.error("API_NG_SIGN_REQUEST: {} PARTICIPANT: {}", signRequest, participant);
      throw new IllegalArgumentException("Requesting to sign with invalid arguments");
    }

    this.signDocuments(context, project.getSessionId(), signRequest, project.getSignatureLevel());
    this.updateProjectAfterSign(participant.getId(), project.getDocuments(), DocumentStatus.SIGNED);

    // Store value after signed by this participant
    participant.setSigned(true);
    participant.setActionedDate(Date.from(Instant.now()));

    // Set property to update json file in the next step
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.SIGNED_DOCUMENT);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void signDocuments(
      ExecutionContext context, Long sessionId, SignRequest signRequest, String signatureLevel) {
    try {
      var signRes =
          this.apiNgFeignClient.signDocumentsWithLevel(sessionId, signRequest, signatureLevel);
      if (Objects.nonNull(signRes) && signRes.containsKey(ActorActionConstant.STATUS_KEY)) {
        if (Objects.equals(signRes.get(ActorActionConstant.STATUS_KEY), ActorActionConstant.DONE)) {
          log.warn("This signatory is already signed");
        } else if (Objects.equals(
            signRes.get(ActorActionConstant.STATUS_KEY), ActorActionConstant.IN_PROGRESS)) {
          log.warn("This signatory is signing the document");
          throw new InternalErrorException(
              "This signatory is signing the document in background process");
        }
      }
      this.updateParticipantStatus(context, DocumentStatus.SIGNED);
    } catch (Exception e) {
      this.updateParticipantStatus(context, DocumentStatus.RETRY);
      log.error("Could not sign document: ", e);
      throw new InternalErrorException("Error when sign document: " + e.getMessage());
    }
  }

  private void updateProjectAfterSign(
      Long participantId, List<Document> documents, DocumentStatus status) {
    var signatory = new SignatoryRequest(participantId, status);
    this.projectFeignClient.updateProjectAfterSigned(
        new ProjectAfterSignRequest(
            signatory, documents.stream().map(DocumentRequest::new).toList()));
  }

  private void updateParticipantStatus(ExecutionContext context, DocumentStatus status) {
    context.put(SignProcessConstant.PARTICIPANT_DOCUMENT_STATUS, status);
    this.updateToProcessingHandler.execute(context);
  }
}
