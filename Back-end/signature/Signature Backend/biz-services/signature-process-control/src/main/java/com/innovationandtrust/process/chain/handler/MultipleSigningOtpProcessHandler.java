package com.innovationandtrust.process.chain.handler;

import static com.innovationandtrust.process.constant.SignProcessConstant.MAX_ATTEMPTS;
import static com.innovationandtrust.process.utils.ProcessControlUtils.getParticipant;
import static com.innovationandtrust.process.utils.ProcessControlUtils.getProject;
import static com.innovationandtrust.process.utils.ProcessControlUtils.validateAndGetPhone;

import com.innovationandtrust.process.chain.handler.helper.OtpProcessingHelper;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.OtpProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.ProcessingDto;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.GeneratedOTP;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.GenerateOtpRequest;
import com.innovationandtrust.utils.aping.model.SessionActor;
import com.innovationandtrust.utils.aping.model.ValidateOtpRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** This class is about multiple projects OTP process. */
@Slf4j
@Component
public class MultipleSigningOtpProcessHandler extends AbstractExecutionHandler {
  private final OtpProcessingHelper otpProcessingHelper;
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final NotificationFeignClient notificationFeignClient;
  private final ProfileFeignClient profileFeignClient;

  /**
   * Contractor of the class.
   *
   * @param otpProcessingHelper helper class for validate OTP
   * @param apiNgFeignClient open feign endpoints of api ng service
   * @param notificationFeignClient open feign signature-notification service
   * @param profileFeignClient open feign endpoints of profile-management service
   */
  public MultipleSigningOtpProcessHandler(
      OtpProcessingHelper otpProcessingHelper,
      ApiNgFeignClientFacade apiNgFeignClient,
      NotificationFeignClient notificationFeignClient,
      ProfileFeignClient profileFeignClient) {
    this.otpProcessingHelper = otpProcessingHelper;
    this.apiNgFeignClient = apiNgFeignClient;
    this.notificationFeignClient = notificationFeignClient;
    this.profileFeignClient = profileFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("[MultipleSigningOtpProcessHandler Executing]");
    final var action =
        context.getOrElse(
            SignProcessConstant.OTP_PROCESS_ACTION,
            OtpProcessAction.class,
            OtpProcessAction.GENERATE);
    var requests =
        CommonUsages.convertToList(
            context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS), SigningProcessDto.class);
    var projects =
        CommonUsages.convertToList(context.get(SignProcessConstant.PROJECTS), Project.class);

    if (!projects.isEmpty()) {
      switch (Objects.requireNonNull(action)) {
        case VALIDATE_PHONE_NUMBER -> this.validatePhoneNumber(
            requests, projects, context.get(SignProcessConstant.PHONE_NUMBER, String.class));
        case VALIDATE -> this.validateOpt(
            requests, projects, context.get(SignProcessConstant.OTP_VALUE, String.class));
        default -> this.generateOpt(requests, projects);
      }

      context.put(SignProcessConstant.PROJECTS, projects);
      context.put(
          SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE_MULTIPLE);

      // For returning
      context.put(SignProcessConstant.VALID_PHONE, getValidPhone(requests, projects));
      context.put(
          SignProcessConstant.VALID_OTP,
          ProcessControlUtils.getOtpInfo(getOtp(requests, projects)));
      context.put(
          SignProcessConstant.PROJECTS_FAIL_VALIDATED, getMaxFailedProjects(requests, projects));
    }

    return ExecutionState.NEXT;
  }

  private void validatePhoneNumber(
      List<SigningProcessDto> requests, List<Project> projects, String missingDigits) {
    var phone = getPhone(validateAndGetPhone(requests, projects));

    List<ProcessingDto> processingList = new ArrayList<>();

    requests.forEach(
        (SigningProcessDto request) -> {
          var project = getProject(projects, request.getFlowId());
          var participant = getParticipant(project, request.getUuid());
          processingList.add(
              ProcessingDto.builder().project(project).participant(participant).build());
        });

    this.otpProcessingHelper.validatePhoneNumber(processingList, missingDigits, phone);
  }

  private void generateOpt(List<SigningProcessDto> requests, List<Project> projects) {
    var phone = getPhone(validateAndGetPhone(requests, projects));

    var otpRequest = new GenerateOtpRequest();

    requests.forEach(
        (SigningProcessDto request) -> {
          var project = getProject(projects, request.getFlowId());
          var participant = getParticipant(project, request.getUuid());
          if (participant.isSigner()) {
            ProcessControlUtils.isAdvancedProject(participant, project.getSignatureLevel());
          }

          var sessions = otpRequest.getSessions();
          sessions.add(buildSessionActor(project, participant));
        });

    var response = this.apiNgFeignClient.generateOTP(otpRequest);
    var otp = new GeneratedOTP(response.getOtp(), response.getDate(), response.getExpires(), 0);

    requests.forEach(
        (SigningProcessDto request) -> {
          var project = getProject(projects, request.getFlowId());
          var participant = getParticipant(project, request.getUuid());
          otp.setErrorValidation(
              Objects.nonNull(participant.getOtp())
                  ? participant.getOtp().getErrorValidation()
                  : 0);
          participant.setOtp(otp);
        });

    this.notificationFeignClient.sendSms(
        phone, String.format("Electronic signature Passcode: %s", response.getOtp()));
  }

  private static SessionActor buildSessionActor(Project project, Participant participant) {
    var template = project.getTemplate();
    return SessionActor.builder()
        .sessionId(project.getSessionId())
        .actor(participant.getActorUrl())
        .documents(project.getDocumentUrls())
        .tag(template.getSignProcess().getVal())
        .build();
  }

  private void validateOpt(
      List<SigningProcessDto> requests, List<Project> projects, String otpCode) {
    var validateOptRequest = new ValidateOtpRequest();
    validateOptRequest.setOtp(otpCode);
    validateOptRequest.setDelete(true);

    requests.forEach(
        (SigningProcessDto request) -> {
          var project = getProject(projects, request.getFlowId());
          var participant = getParticipant(project, request.getUuid());
          var sessions = validateOptRequest.getSessions();
          sessions.add(buildSessionActor(project, participant));
        });

    var isValid = false;

    try {
      var response = this.apiNgFeignClient.validateOTP(validateOptRequest);
      // if delete = true when check otp at api-ng, will response null
      log.info("OTP validation response: {}", response.getOtp());
      // try success means true
      isValid = true;
    } catch (FeignClientRequestException ex) {
      log.error("[API_NG OPT Validation] : ", ex);
    }

    List<Long> blockUsers = new ArrayList<>();
    boolean finalIsValid = isValid;
    requests.forEach(
        (SigningProcessDto request) -> {
          var project = getProject(projects, request.getFlowId());
          var participant = getParticipant(project, request.getUuid());
          participant.getOtp().setValidated(finalIsValid);
          invalidOtp(blockUsers, participant);
        });

    this.otpProcessingHelper.updateDocumentsStatus(blockUsers);
  }

  private static void invalidOtp(List<Long> blockUsers, Participant participant) {
    if (!participant.getOtp().isValidated()) {
      participant.getOtp().setErrorValidation(participant.getOtp().getErrorValidation() + 1);
      if (participant.getOtp().getErrorValidation() == MAX_ATTEMPTS) {
        blockUsers.add(participant.getId());
      }
    }
  }

  private String getPhone(Set<String> phones) {
    if (phones.size() > 1) {
      log.info("[Found multiple phone number] : {}", phones);
      final var user = this.profileFeignClient.getOwnInfo();
      return user.getPhone();
    }

    return phones.iterator().next();
  }

  /** To get a validation that greatest attempts. */
  private GeneratedOTP getOtp(List<SigningProcessDto> requests, List<Project> projects) {
    log.info("Getting OTP that has greatest total errors.");
    return requests.stream()
        .map(
            (SigningProcessDto request) -> {
              var project = getProject(projects, request.getFlowId());
              var participant = getParticipant(project, request.getUuid());
              return participant.getOtp();
            })
        .filter(Objects::nonNull)
        .max(Comparator.comparingInt(GeneratedOTP::getErrorValidation))
        .orElse(null);
  }

  /** To get a validation that greatest attempts. */
  private Participant.ValidPhone getValidPhone(
      List<SigningProcessDto> requests, List<Project> projects) {
    log.info("Getting Valid phone that has greatest total attempts.");
    var stream =
        requests.stream()
            .map(
                (SigningProcessDto request) -> {
                  var project = getProject(projects, request.getFlowId());
                  var participant = getParticipant(project, request.getUuid());
                  return participant.getValidPhone();
                });

    AtomicReference<Participant.ValidPhone> validPhone = new AtomicReference<>(null);
    stream
        .filter(Objects::nonNull)
        .max(Comparator.comparingInt(Participant.ValidPhone::getTotalAttempts))
        .ifPresent(
            (Participant.ValidPhone value) -> {
              validPhone.set(value);
              final var phones = ProcessControlUtils.getPhones(requests, projects);
              if (!phones.isEmpty() && (phones.size() > 1) && validPhone.get().isValid()) {
                validPhone.get().setNumber(getPhone(phones));
              }
            });

    return validPhone.get();
  }

  /** To get projects that blocked to participant who validated otp/phone at maximum attempts. */
  private List<String> getMaxFailedProjects(
      List<SigningProcessDto> requests, List<Project> projects) {

    log.info("Getting max failed projects by participants");
    List<String> failedProjects = new ArrayList<>();
    requests.forEach(
        (SigningProcessDto request) -> {
          var project = getProject(projects, request.getFlowId());
          var participant = getParticipant(project, request.getUuid());
          if (checkIsMaxValidation(participant)) {
            failedProjects.add(request.getFlowId());
          }
        });

    return failedProjects;
  }

  private static boolean checkIsMaxValidation(Participant participant) {
    final var otp = participant.getOtp();
    final var validPhone = participant.getValidPhone();

    final var isMaxOtp = Objects.nonNull(otp) && otp.getErrorValidation() == MAX_ATTEMPTS;
    final var isMaxValidPhone =
        Objects.nonNull(validPhone) && validPhone.getTotalAttempts() == MAX_ATTEMPTS;

    return isMaxOtp || isMaxValidPhone;
  }
}
