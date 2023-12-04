package com.innovationandtrust.process.utils;

import static com.innovationandtrust.process.constant.SignProcessConstant.MAX_ATTEMPTS;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.exception.MissingCompanyInfoException;
import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.model.email.EmailInvitationRequestInterface;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.GeneratedOTP;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.ForbiddenRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.mail.provider.MailServiceProvider;
import jakarta.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessControlUtils {

  public static final String INVALID_PARTICIPANT =
      "In project flowId: %s doesn't have participant with uuid: %s";

  public static ExecutionContext getProject(String flowId, String uuid) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    context.put(SignProcessConstant.PARTICIPANT_ID, uuid);
    return context;
  }

  public static ExecutionContext getProjects(List<SigningProcessDto> requests) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.MULTI_SIGNING_PROJECTS, requests);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ_MULTIPLE);
    return context;
  }

  public static Project getProject(ExecutionContext context) {
    return context
        .find(SignProcessConstant.PROJECT_KEY, Project.class)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The project file flow is require and cannot be null;"));
  }

  public static String buildInvitationUrl(
      String flowId, String uuid, String frontUrl, String contextPath, String companyUuid) {
    if (StringUtils.hasText(companyUuid)) {
      return String.format(
          "%s/%s/%s?uuid=%s&company=%s", frontUrl, contextPath, flowId, uuid, companyUuid);
    }
    return String.format("%s/%s/%s?uuid=%s&company=", frontUrl, contextPath, flowId, uuid);
  }

  public static String buildInvitationParams(String flowId, String uuid, String companyUuid) {
    return String.format("flowId=%s&uuid=%s&company=%s", flowId, uuid, companyUuid);
  }

  public static MimeMessage getMessage(
      MailRequest request, Resource logo, MailServiceProvider mailServiceProvider) {
    if (logo == null) {
      return mailServiceProvider.prepareMimeMessage(
          request,
          Map.of(
              EmailInvitationRequestInterface.LOGO_KEY,
              new ClassPathResource(EmailInvitationRequestInterface.LOGO_PATH.toString())));
    }
    return mailServiceProvider.prepareMimeMessageAttachment(
        request, Map.of(EmailInvitationRequestInterface.LOGO_KEY, logo), "image/png");
  }

  public static String convertHtmlToString(String html) {
    var doc = Jsoup.parse(html);
    return doc.body().wholeText();
  }

  /**
   * To check if corporate info was null.
   *
   * @param corporateInfo refers to corporate info {@link CorporateInfo}
   * @param flowId refers to project flow id
   */
  public static void checkCompanyInfo(CorporateInfo corporateInfo, String flowId) {
    if (!Objects.nonNull(corporateInfo)) {
      log.error("Company was null ... ");
      throw new MissingCompanyInfoException(flowId);
    }
  }

  public static void checkIsCanceled(String status) {
    if (Objects.equals(status, ProjectStatus.ABANDON.name())) {
      throw new ForbiddenRequestException("Invalid action. Project is cancelled...");
    }
  }

  public static String getFilename(String flowId) {
    return String.format("%s.%s", flowId, "json");
  }

  /**
   * @param requests refers multiple project flowId with signer uuid
   * @param projects have been read by requests from json files
   * @return phone unique number of signer
   */
  public static Set<String> validateAndGetPhone(
      List<SigningProcessDto> requests, List<Project> projects) {
    final var maxValidatedMsg =
        "This participant %s of project %s has been validated %s max of total attempts.";

    Set<String> phone = new HashSet<>();
    Set<String> signatureLevels = new HashSet<>();
    Set<String> role = new HashSet<>();
    AtomicBoolean isMaxValidatedPhone = new AtomicBoolean(false);
    AtomicBoolean isMaxOtpValidated = new AtomicBoolean(false);
    AtomicReference<String> flowId = new AtomicReference<>();
    AtomicReference<String> uuid = new AtomicReference<>();

    requests.forEach(
        request ->
            projects.stream()
                .filter(project -> Objects.equals(project.getFlowId(), request.getFlowId()))
                .findFirst()
                .ifPresent(
                    project ->
                        project
                            .getParticipantByUuid(request.getUuid())
                            .ifPresentOrElse(
                                participant -> {
                                  var validPhone = participant.getValidPhone();
                                  var otp = participant.getOtp();

                                  signatureLevels.add(project.getSignatureLevel());
                                  phone.add(participant.getPhone());
                                  role.add(participant.getRole());

                                  isMaxValidatedPhone.set(
                                      Objects.nonNull(validPhone)
                                          && Objects.equals(
                                              participant.getValidPhone().getTotalAttempts(),
                                              MAX_ATTEMPTS));

                                  isMaxValidatedPhone.set(
                                      Objects.nonNull(otp)
                                          && Objects.equals(
                                              participant.getValidPhone().getTotalAttempts(),
                                              MAX_ATTEMPTS));

                                  flowId.set(request.getFlowId());
                                  uuid.set(request.getUuid());
                                },
                                () -> invalidParticipant(request.getFlowId(), request.getUuid()))));

    isNotSimpleProject(signatureLevels);

    if (signatureLevels.size() > 1) {
      throw new InvalidRequestException(
          "Invalid requests. Only one signature level allow for multiple sign. Found "
              + signatureLevels);
    } else if (role.size() > 1
        || !Objects.equals(role.iterator().next(), RoleConstant.ROLE_SIGNATORY)) {
      throw new InvalidRequestException(
          "Invalid participant role. Only role signatory allow. Found " + role);
    } else if (isMaxValidatedPhone.get()) {
      throw new InvalidRequestException(
          String.format(maxValidatedMsg, uuid.get(), flowId.get(), " phone "));
    } else if (isMaxOtpValidated.get()) {
      throw new InvalidRequestException(
          String.format(maxValidatedMsg, uuid.get(), flowId.get(), " OTP "));
    }

    return phone;
  }

  public static Set<String> getPhones(List<SigningProcessDto> requests, List<Project> projects) {
    Set<String> phones = new HashSet<>();
    requests.forEach(
        request ->
            projects.stream()
                .filter(project -> Objects.equals(project.getFlowId(), request.getFlowId()))
                .findFirst()
                .flatMap(project -> project.getParticipantByUuid(request.getUuid()))
                .ifPresent(participant -> phones.add(participant.getPhone())));

    return phones;
  }

  private static void isNotSimpleProject(Set<String> signatureLevels) {
    signatureLevels.forEach(
        level -> {
          if (!Objects.equals(SignatureSettingLevel.SIMPLE.name(), level)) {
            throw new InvalidRequestException(
                "There is no configuration with signature level: " + level);
          }
        });
  }

  public static void invalidParticipant(String flowId, String uuid) {
    throw new InvalidRequestException(String.format(INVALID_PARTICIPANT, flowId, uuid));
  }

  public static OtpInfo getOtpInfo(GeneratedOTP otp) {
    var optInfo = new OtpInfo();
    if (Objects.nonNull(otp) && StringUtils.hasText(otp.getExpires())) {
      var date = ZonedDateTime.parse(otp.getExpires());
      optInfo.setExpired(ZonedDateTime.now(date.getZone()).isBefore(date));
      optInfo.setValidated(otp.isValidated());
      optInfo.setTotalError(otp.getErrorValidation());
    }
    return optInfo;
  }

  public static boolean isExpired(Date expireDate, String status) {
    final var excludeStatuses =
        List.of(
            ProjectStatus.COMPLETED.name(),
            ProjectStatus.ABANDON.name(),
            ProjectStatus.EXPIRED.name());
    final boolean validStatus = !excludeStatuses.contains(status);
    final boolean isExpired = Objects.nonNull(expireDate) && expireDate.before(new Date());

    return validStatus && isExpired;
  }

  public static void  isAdvancedProject(Participant participant, String signatureLevel) {
    if (Objects.equals(signatureLevel, SignatureSettingLevel.ADVANCE.getValue())
            && !participant.isDocumentVerified()) {
      throw new InvalidRequestException(
              "Project ADVANCE must finished document verify before OPT validation...");
    }
  }

  public static Project getProject(List<Project> projects, String flowId) {
    var projectOtp =
            projects.stream()
                    .filter(project -> Objects.equals(project.getFlowId(), flowId))
                    .findFirst();

    if (projectOtp.isEmpty()) {
      throw new InvalidRequestException("Project not found flowId:" + flowId);
    }

    return projectOtp.get();
  }

  public static Participant getParticipant(Project project, String uuid) {
    var participantOtp = project.getParticipantByUuid(uuid);
    if (participantOtp.isEmpty()) {
      throw new InvalidRequestException(
              String.format(INVALID_PARTICIPANT, project.getFlowId(), uuid));
    }

    return participantOtp.get();
  }
}
