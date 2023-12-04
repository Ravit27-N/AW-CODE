/*
 * Process of requesting to sign.
 */
package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.constant.EIDSmsTemplate;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.eid.EIDProperty;
import com.innovationandtrust.utils.eid.model.ChallengeCodeDto;
import com.innovationandtrust.utils.eid.model.MessageSmsDto;
import com.innovationandtrust.utils.eid.model.RequestSignViaSmsRequest;
import com.innovationandtrust.utils.eid.model.RequestSignViaSmsResponse;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*Current execution context which holds all the state from previous execution and */
@Component
@Slf4j
public class RequestToSignHandler extends AbstractExecutionHandler {

  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final int ttl;
  private final String ttlUnit;
  private final String externalReference;
  private final String challengeCodeCharset;
  private final int challengeCodeLength;

  public RequestToSignHandler(
      ApiNgFeignClientFacade apiNgFeignClient, @Autowired @NotNull EIDProperty eIDProperty) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.ttl = eIDProperty.getTtl();
    this.externalReference = eIDProperty.getExternalReference();
    this.ttlUnit = eIDProperty.getTtlUnit();
    this.challengeCodeCharset = eIDProperty.getChallengeCodeCharset();
    this.challengeCodeLength = eIDProperty.getChallengeCodeLength();
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {

    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    if (!Objects.equals(SignatureSettingLevel.QUALIFY.getValue(), project.getSignatureLevel())) {
      log.warn(
          "[RequestToSignHandler] Request to sign with EID is support only Qualify Signature.");
      return ExecutionState.END;
    }

    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            (Participant participant) -> {
              final var MAX_OTP_ERROR = 3;
              final var errorValidationOtp = participant.getErrorValidationOtp();

              if (errorValidationOtp >= MAX_OTP_ERROR) {
                log.warn("[RequestToSignHandler] OTP code failed 3 time. Cannot request sign.");
                throw new BadRequestException(
                    "Cannot request sign (OTP validation has been tried many times).");
              }

              this.requestSign(context, project, participant);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);

    return ExecutionState.NEXT;
  }

  private void requestSign(ExecutionContext context, Project project, Participant participant) {
    try {
      final RequestSignViaSmsRequest request = this.createRequest(project, participant);
      final RequestSignViaSmsResponse response =
          this.apiNgFeignClient.requestSignDocumentViaSms(project.getSessionId(), request);
      participant.setRequestSign(true);
      context.put(SignProcessConstant.REQUEST_SIGN, response);
    } catch (RuntimeException e) {
      log.error("[RequestToSignHandler] error while request signature document:", e);
      throw new BadRequestException("Process request signature document error.");
    }
  }

  private RequestSignViaSmsRequest createRequest(Project project, Participant participant) {

    final var messageSmsDto = createMessageSmsDto(participant.getPhone());
    final var challengeCodeDto = this.createChallengeCodeDto();

    return RequestSignViaSmsRequest.builder()
        .actor(participant.getActorUrl())
        .documents(project.getDocumentUrls())
        .tag(project.getTemplate().getSignProcess().getVal())
        .identityId(participant.getIdentityId())
        .message(messageSmsDto)
        .challengeCode(challengeCodeDto)
        .ttl(this.ttl)
        .ttlUnit(this.ttlUnit)
        .externalReference(this.externalReference)
        .build();
  }

  private static MessageSmsDto createMessageSmsDto(String phone) {
    return MessageSmsDto.builder()
        .message(EIDSmsTemplate.MESSAGE)
        .phone(phone)
        .from(EIDSmsTemplate.MESSAGE_FROM)
        .build();
  }

  private ChallengeCodeDto createChallengeCodeDto() {
    return new ChallengeCodeDto(this.challengeCodeCharset, this.challengeCodeLength);
  }
}
