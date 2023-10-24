package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.OtpProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.process.model.email.FailedValidatePhoneMailModel;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.PhoneNumberUtils;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.GeneratedOTP;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.user.User;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.GenerateOtpRequest;
import com.innovationandtrust.utils.aping.model.ValidateOtpRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/**
 * Handling the process of generating and validating OTP code.
 *
 * @author Vichet CHANN
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpProcessingHandler extends AbstractExecutionHandler {

  private final TemplateEngine templateEngine;
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final NotificationFeignClient notificationFeignClient;
  private final SignatureIdentityVerificationFeignClient verificationFeignClient;
  private final ProfileFeignClient profileFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private static final int OTP_VALIDATE_ATTEMPTS = 3;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    var action =
        context.getOrElse(
            SignProcessConstant.OTP_PROCESS_ACTION,
            OtpProcessAction.class,
            OtpProcessAction.GENERATE);
    switch (Objects.requireNonNull(action)) {
      case VALIDATE_PHONE_NUMBER -> this.validatePhoneNumber(
          project, uuid, context.get(SignProcessConstant.PHONE_NUMBER, String.class));
      case VALIDATE -> project
          .getParticipantByUuid(uuid)
          .ifPresent(
              person -> {
                if (person.getOtp().getErrorValidation() >= OTP_VALIDATE_ATTEMPTS) {
                  throw new BadRequestException("OTP validation has been tried many times.");
                }
                var otpCode = context.get(SignProcessConstant.OTP_VALUE, String.class);
                if (person.isSigner()) {
                  this.validateSignOtp(project, uuid, otpCode);
                }
                if (person.isApprover()) {
                  this.validateApprovalOtp(project, uuid, otpCode);
                }
              });
      default -> this.generateOtp(project, uuid);
    }
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  /**
   * Validate Phone number.
   *
   * @param project refers to project has to be signed
   * @param uuid refers to user's uuid
   * @param phoneNumber refers to user's phone number
   */
  private void validatePhoneNumber(Project project, String uuid, String phoneNumber) {
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            p -> {
              var validPhone = p.getValidPhone();
              var checkPhone =
                  PhoneNumberUtils.verifyPhoneNumber(
                      p.getPhone(), phoneNumber, validPhone.getMissingLength());
              validPhone.setValid(checkPhone);
              if (checkPhone) {
                validPhone.setNumber(p.getPhone());
                if (Objects.equals(
                        project.getSignatureLevel(), SignatureSettingLevel.ADVANCE.getValue())
                    && p.isSigner()) {
                  this.verificationFeignClient.validateDossier(p.getDossierId());
                }
              } else {
                validPhone.setTotalAttempts(validPhone.getTotalAttempts() + 1);
                validPhone.setNumber("");

                if (validPhone.getTotalAttempts() >= 3) {
                  if (Objects.nonNull(project.getAssignedTo())) {
                    // send mail
                    this.profileFeignClient
                        .findByIdOptional(project.getAssignedTo())
                        .ifPresentOrElse(
                            user -> this.sendMail(user, project),
                            () -> log.warn("Cannot send mail to assign user..."));
                  } else {
                    // send mail
                    this.profileFeignClient
                        .findByIdOptional(project.getCreatedBy())
                        .ifPresentOrElse(
                            user -> this.sendMail(user, project),
                            () -> log.warn("Cannot send mail to assign user..."));
                  }
                }
              }
              p.setValidPhone(validPhone);
            });
  }

  private void sendMail(User user, Project project) {
    var logoFile =
        this.corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());
    var company = project.getCorporateInfo();

    // Set value to EmailParametersModel
    EmailParametersModel emailParametersModel =
        new EmailParametersModel(
            user.getFullName(),
            project.getName(),
            null,
            "Échec de la validation du numéro de téléphone",
            null,
            user.getEmail());

    var request =
        new FailedValidatePhoneMailModel(
            emailParametersModel, null, company.getMainColor(), logoFile);

    Executors.newSingleThreadExecutor()
        .execute(
            () ->
                this.notificationFeignClient.sendMail(
                    request.getMailRequest(templateEngine), company.getLogo()));
  }

  private void generateOtp(Project project, String uuid) {
    var otp = new GenerateOtpRequest();
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            value -> {
              if (value.isSigner()) {
                this.isAdvancedProject(value, project.getSignatureLevel());
              }
              var template = project.getTemplate();
              otp.setActor(value.getActorUrl());
              otp.setDocuments(project.getDocumentUrls());
              otp.setTag(
                  value.isSigner()
                      ? template.getSignProcess().getVal()
                      : template.getApprovalProcess().getVal());
              var response =
                  this.apiNgFeignClient.generateOTP(project.getDetail().getSessionId(), otp);
              value.setOtp(
                  new GeneratedOTP(
                      response.getOtp(),
                      response.getDate(),
                      response.getExpires(),
                      Objects.nonNull(value.getOtp()) ? value.getOtp().getErrorValidation() : 0));
              if (Objects.equals(value.getRole(), ParticipantRole.SIGNATORY.getRole())) {
                this.notificationFeignClient.sendSms(
                    value.getPhone(),
                    String.format("Electronic signature Passcode: %s", response.getOtp()));
              }
            });
  }

  private void validateApprovalOtp(Project project, String uuid, String otp) {
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            person -> {
              var validate =
                  this.validateOtp(
                      new ValidateOtpRequest(
                          otp, person.getActorUrl(), project.getDocumentUrls(), true),
                      project.getSessionId());
              person.getOtp().setValidated(validate);
              if (!validate) {
                person.getOtp().setErrorValidation(person.getOtp().getErrorValidation() + 1);
              }
            });
  }

  private void validateSignOtp(Project project, String uuid, String otp) {
    var optValidation = new ValidateOtpRequest();
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            participant -> {
              // Prepare to require information before validate OPT code and signing process
              optValidation.setOtp(otp);
              optValidation.setActor(participant.getActorUrl());
              optValidation.setDocuments(project.getDocumentUrls());
              optValidation.setDelete(true);
              var validate = this.validateOtp(optValidation, project.getSessionId());
              participant.getOtp().setValidated(validate);
              if (!validate) {
                participant
                    .getOtp()
                    .setErrorValidation(participant.getOtp().getErrorValidation() + 1);
              }
            });
  }

  private boolean validateOtp(ValidateOtpRequest request, Long sessionId) {
    try {
      var response = this.apiNgFeignClient.validateOTP(sessionId, request);
      // if delete = true when check otp at api-ng, will response null
      log.info("OTP validation response: {}", response.getOtp());
      // try success means true
      return true;
    } catch (FeignClientRequestException ex) {
      return false;
    }
  }

  private void isAdvancedProject(Participant participant, String signatureLevel) {
    if (Objects.equals(signatureLevel, SignatureSettingLevel.ADVANCE.getValue())
        && !participant.isDocumentVerified()) {
      throw new InvalidRequestException(
          "Project ADVANCE must finished document verify before OPT validation...");
    }
  }
}
