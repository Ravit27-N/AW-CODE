package com.innovationandtrust.process.chain.handler.sign;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.DocumentInfo;
import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.model.SignInfo.Actor;
import com.innovationandtrust.process.model.SignInfo.PhoneNumber;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.PhoneNumberUtils;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class SigningInfoHandler extends AbstractExecutionHandler {

  private final ProfileFeignClient profileFeignClient;

  private final ProcessControlProperty property;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    return this.documentInfo(context);
  }

  private ExecutionState documentInfo(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var signInfo = new SignInfo();
    signInfo.setProjectName(project.getName());
    signInfo.setSignatureLevel(project.getSignatureLevel());
    signInfo.setProjectStatus(project.getStatus());
    signInfo.setFlowId(project.getFlowId());
    signInfo.setProjectName(project.getName());
    signInfo.setSigningProcess(project.getTemplate().getSignProcess().getVal());
    var user = this.profileFeignClient.getUserInfo(project.getCreatedBy());
    signInfo.setCreatorInfo(user);
    signInfo.setDocuments(project.getDocuments().stream().map(DocumentInfo::new).toList());
    var participantId = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    signInfo.setUuid(participantId);
    AtomicReference<ExecutionState> state = new AtomicReference<>(ExecutionState.NEXT);
    signInfo.setSetting(project.getSetting());
    project
        .getParticipantByUuid(participantId)
        .ifPresent(
            value -> {
              signInfo.setActor(
                  new Actor(
                      value.getFirstName(),
                      value.getLastName(),
                      value.getRole(),
                      value.getProcessed(),
                      value.getComment(),
                      value.getSignatureMode(),
                      value.getSignatureImage(),
                      value.isDocumentVerified()));
              var missingLength = property.getPhoneNumber().getMissingLength();
              signInfo.setInvitationDate(value.getInvitationDate());
              var validPhone = value.getValidPhone();
              if (Objects.nonNull(validPhone) && validPhone.getMissingLength() > 0) {
                signInfo.setPhoneNumber(new PhoneNumber(validPhone));
                state.set(ExecutionState.END);
              } else {
                value
                    .getValidPhone()
                    .setMissingLength(property.getPhoneNumber().getMissingLength());
                signInfo.setPhoneNumber(new PhoneNumber());
                context.put(
                    SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
              }
              signInfo
                  .getPhoneNumber()
                  .setRemovedNumber(
                      PhoneNumberUtils.removePhoneNumber(value.getPhone(), missingLength));
              signInfo.getPhoneNumber().setMissingLength(missingLength);
              signInfo.setAllSigned(!project.hasNextParticipant());
              this.checkValidOTP(value, signInfo);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.SIGNING_INFO, signInfo);
    return state.get();
  }

  private void checkValidOTP(Participant participant, SignInfo signInfo) {
    var generatedOtp = participant.getOtp();
    var optInfo = new OtpInfo();
    if (Objects.nonNull(generatedOtp) && StringUtils.hasText(generatedOtp.getExpires())) {
      var date = ZonedDateTime.parse(generatedOtp.getExpires());
      optInfo.setExpired(ZonedDateTime.now(date.getZone()).isBefore(date));
      optInfo.setValidated(generatedOtp.isValidated());
      optInfo.setTotalError(generatedOtp.getErrorValidation());
    }
    signInfo.setOtpInfo(optInfo);
  }
}
