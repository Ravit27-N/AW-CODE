package com.innovationandtrust.process.chain.handler.sign;

import static com.innovationandtrust.process.utils.ProcessControlUtils.isExpired;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.DocumentInfo;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.model.SignInfo.Actor;
import com.innovationandtrust.process.model.SignInfo.PhoneNumber;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.PhoneNumberUtils;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class SigningInfoHandler extends AbstractExecutionHandler {

  private final ProfileFeignClient profileFeignClient;
  private final ProcessControlProperty property;

  public SigningInfoHandler(
      ProfileFeignClient profileFeignClient, ProcessControlProperty property) {
    this.profileFeignClient = profileFeignClient;
    this.property = property;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    return this.documentInfo(context);
  }

  private ExecutionState documentInfo(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var participantId = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    var participantOpt = project.getParticipantByUuid(participantId);

    if (participantOpt.isEmpty()) {
      throw new EntityNotFoundException(
          "Unable to find participant with this id: " + participantId);
    }
    var participant = participantOpt.get();

    var user = this.profileFeignClient.getUserInfo(project.getCreatedBy());

    var signInfo = new SignInfo();
    signInfo.setCreatorInfo(user);
    signInfo.setProjectName(project.getName());
    signInfo.setSignatureLevel(project.getSignatureLevel());
    signInfo.setProjectStatus(project.getStatus());
    signInfo.setFlowId(project.getFlowId());
    signInfo.setProjectName(project.getName());
    signInfo.setSigningProcess(project.getTemplate().getSignProcess().getVal());
    signInfo.setDocuments(project.getDocuments().stream().map(DocumentInfo::new).toList());
    signInfo.setUuid(participantId);
    signInfo.setSetting(project.getSetting());
    signInfo.setAllSigned(!project.hasNextParticipant());
    signInfo.setIdentityId(participant.getIdentityId());
    signInfo.setRequestToSign(participant.getRequestSign());
    signInfo.setErrorValidationOtp(participant.getErrorValidationOtp());
    signInfo.setInvitationDate(participant.getInvitationDate());
    signInfo.setActor(
        new Actor(
            participant.getFirstName(),
            participant.getLastName(),
            participant.getRole(),
            participant.getProcessed(),
            participant.getComment(),
            participant.getSignatureMode(),
            participant.getSignatureImage(),
            participant.isDocumentVerified(),
            participant.isProcessing(),
            participant.getVideoVerifiedStatus()));

    var state = ExecutionState.NEXT;
    var validPhone = participant.getValidPhone();

    if (Objects.nonNull(validPhone) && validPhone.getMissingLength() > 0) {
      signInfo.setPhoneNumber(new PhoneNumber(validPhone));
      state = ExecutionState.END;
    } else {
      participant.getValidPhone().setMissingLength(property.getPhoneNumber().getMissingLength());
      signInfo.setPhoneNumber(new PhoneNumber());
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    }

    if (isExpired(project.getDetail().getExpireDate(), project.getStatus())) {
      project.setStatus(ProjectStatus.EXPIRED.name());
      signInfo.setProjectStatus(project.getStatus());
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      state = ExecutionState.NEXT;
    }

    var missingLength = property.getPhoneNumber().getMissingLength();
    signInfo
        .getPhoneNumber()
        .setRemovedNumber(
            PhoneNumberUtils.removePhoneNumber(participant.getPhone(), missingLength));
    signInfo.getPhoneNumber().setMissingLength(missingLength);
    signInfo.setOtpInfo(ProcessControlUtils.getOtpInfo(participant.getOtp()));
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.SIGNING_INFO, signInfo);

    return state;
  }
}
