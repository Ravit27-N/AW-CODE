package com.innovationandtrust.process.chain.handler;

import static com.innovationandtrust.process.constant.SignProcessConstant.MAX_ATTEMPTS;
import static com.innovationandtrust.process.utils.ProcessControlUtils.INVALID_PARTICIPANT;

import com.innovationandtrust.process.chain.handler.helper.OtpProcessingHelper;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.OtpProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.ProcessingDto;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.model.project.GeneratedOTP;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.GenerateOtpRequest;
import com.innovationandtrust.utils.aping.model.ValidateOtpRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.Collections;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handling the process of generating and validating OTP code.
 *
 * @author Vichet CHANN
 */
@Slf4j
@Component
public class OtpProcessingHandler extends AbstractExecutionHandler {
  private final OtpProcessingHelper otpProcessingHelper;
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final NotificationFeignClient notificationFeignClient;
  private final MultipleSigningOtpProcessHandler multipleProjects;

  /**
   * Contractor of the class.
   *
   * @param otpProcessingHelper helper class for validate OTP
   * @param apiNgFeignClient open feign endpoints of api ng service
   * @param notificationFeignClient open feign signature-notification service
   * @param multipleProjects handler handle multiple projects OTP processing
   */
  public OtpProcessingHandler(
      OtpProcessingHelper otpProcessingHelper,
      ApiNgFeignClientFacade apiNgFeignClient,
      NotificationFeignClient notificationFeignClient,
      MultipleSigningOtpProcessHandler multipleProjects) {
    this.otpProcessingHelper = otpProcessingHelper;
    this.apiNgFeignClient = apiNgFeignClient;
    this.notificationFeignClient = notificationFeignClient;
    this.multipleProjects = multipleProjects;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    final var action =
        context.getOrElse(
            SignProcessConstant.OTP_PROCESS_ACTION,
            OtpProcessAction.class,
            OtpProcessAction.GENERATE);

    if (Objects.isNull(context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS))) {
      this.project(context, action);
    } else {
      this.multipleProjects.execute(context);
    }

    return ExecutionState.NEXT;
  }

  private void project(ExecutionContext context, OtpProcessAction action) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    final var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    var participantOtp = project.getParticipantByUuid(uuid);
    if (participantOtp.isEmpty()) {
      throw new InvalidRequestException(
          String.format(INVALID_PARTICIPANT, project.getFlowId(), uuid));
    }

    var participant = participantOtp.get();

    switch (Objects.requireNonNull(action)) {
      case VALIDATE_PHONE_NUMBER -> this.otpProcessingHelper.validatePhoneNumber(
          Collections.singletonList(
              ProcessingDto.builder().project(project).participant(participant).build()),
          context.get(SignProcessConstant.PHONE_NUMBER, String.class),
          null);
      case VALIDATE -> this.validateOtp(
          project, participant, context.get(SignProcessConstant.OTP_VALUE, String.class));
      default -> this.generateOtp(project, participant);
    }

    // For response
    context.put(SignProcessConstant.VALID_PHONE, participant.getValidPhone());
    context.put(
        SignProcessConstant.VALID_OTP, ProcessControlUtils.getOtpInfo(participant.getOtp()));
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
  }

  private void generateOtp(Project project, Participant participant) {
    var otp = new GenerateOtpRequest();
    if (participant.isSigner()) {
      ProcessControlUtils.isAdvancedProject(participant, project.getSignatureLevel());
    }

    var template = project.getTemplate();
    otp.setActor(participant.getActorUrl());
    otp.setDocuments(project.getDocumentUrls());
    otp.setTag(
        participant.isSigner()
            ? template.getSignProcess().getVal()
            : template.getApprovalProcess().getVal());

    var response = this.apiNgFeignClient.generateOTP(project.getDetail().getSessionId(), otp);
    participant.setOtp(
        new GeneratedOTP(
            response.getOtp(),
            response.getDate(),
            response.getExpires(),
            Objects.nonNull(participant.getOtp()) ? participant.getOtp().getErrorValidation() : 0));

    if (Objects.equals(participant.getRole(), ParticipantRole.SIGNATORY.getRole())) {
      this.notificationFeignClient.sendSms(
          participant.getPhone(),
          String.format("Electronic signature Passcode: %s", response.getOtp()));
    }
  }

  private void validateOtp(Project project, Participant participant, String otp) {
    if (participant.getOtp().getErrorValidation() >= MAX_ATTEMPTS) {
      throw new BadRequestException("OTP validation has been tried many times.");
    }

    // Prepare to require information before validate OPT code and signing process
    var optValidation = new ValidateOtpRequest();
    optValidation.setOtp(otp);
    optValidation.setActor(participant.getActorUrl());
    optValidation.setDocuments(project.getDocumentUrls());
    optValidation.setDelete(true);

    var validate = this.validateOtp(optValidation, project.getSessionId());
    participant.getOtp().setValidated(validate);
    if (!validate) {
      participant.getOtp().setErrorValidation(participant.getOtp().getErrorValidation() + 1);
    }

    if (participant.getOtp().getErrorValidation() == MAX_ATTEMPTS) {
      this.otpProcessingHelper.updateDocumentsStatus(
          Collections.singletonList(participant.getId()));
    }
  }

  private boolean validateOtp(ValidateOtpRequest request, Long sessionId) {
    try {
      var response = this.apiNgFeignClient.validateOTP(sessionId, request);
      // if delete = true when check otp at api-ng, will response null
      log.info("OTP validation response: {}", response.getOtp());
      // try success means true
      return true;
    } catch (FeignClientRequestException ex) {
      log.error("Error from API NG Service", ex);
      return false;
    }
  }
}
