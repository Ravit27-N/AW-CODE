package com.innovationandtrust.process.chain.handler.helper;

import static com.innovationandtrust.process.constant.SignProcessConstant.MAX_ATTEMPTS;

import com.innovationandtrust.process.model.ProcessingDto;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.process.model.email.FailedValidatePhoneMailModel;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.PhoneNumberUtils;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.user.User;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Component
public class OtpProcessingHelper {
  private final TemplateEngine templateEngine;
  private final ProjectFeignClient projectFeignClient;
  private final SignatureIdentityVerificationFeignClient verificationFeignClient;
  private final ProfileFeignClient profileFeignClient;
  private final NotificationFeignClient notificationFeignClient;

  public OtpProcessingHelper(
      TemplateEngine templateEngine,
      ProjectFeignClient projectFeignClient,
      SignatureIdentityVerificationFeignClient verificationFeignClient,
      ProfileFeignClient profileFeignClient,
      NotificationFeignClient notificationFeignClient) {
    this.templateEngine = templateEngine;
    this.projectFeignClient = projectFeignClient;
    this.verificationFeignClient = verificationFeignClient;
    this.profileFeignClient = profileFeignClient;
    this.notificationFeignClient = notificationFeignClient;
  }

  public void validatePhoneNumber(
      List<ProcessingDto> processingList, String missingDigits, String phone) {
    List<String> dossierIds = new ArrayList<>();
    List<Long> blockUsers = new ArrayList<>();

    processingList.forEach(
        (ProcessingDto processingDto) -> {
          var participant = processingDto.getParticipant();
          var validPhone = participant.getValidPhone();

          final var phoneNumber = StringUtils.hasText(phone) ? phone : participant.getPhone();
          validPhone.setValid(
              PhoneNumberUtils.verifyPhoneNumber(
                  phoneNumber, missingDigits, validPhone.getMissingLength()));
          checkedPhone(dossierIds, blockUsers, validPhone, processingDto);
          participant.setValidPhone(validPhone);
        });

    if (!dossierIds.isEmpty()) {
      this.verificationFeignClient.validateDossiers(dossierIds);
    }

    if (!blockUsers.isEmpty()) {
      this.updateDocumentsStatus(blockUsers);
      this.sendMails(processingList);
    }
  }

  private static void checkedPhone(
      List<String> dossierIds,
      List<Long> blockUsers,
      Participant.ValidPhone validPhone,
      ProcessingDto processingDto) {
    var project = processingDto.getProject();
    var participant = processingDto.getParticipant();

    if (validPhone.isValid()) {
      validPhone.setNumber(participant.getPhone());
      if (isAdvanced(project.getSignatureLevel()) && participant.isSigner()) {
        dossierIds.add(participant.getDossierId());
      }
      return;
    }

    validPhone.setTotalAttempts(validPhone.getTotalAttempts() + 1);
    validPhone.setNumber("");
    if (validPhone.getTotalAttempts() >= MAX_ATTEMPTS) {
      blockUsers.add(participant.getId());

      if (Objects.nonNull(project.getAssignedTo())) {
        processingDto.setUser(getUser(project.getAssignedTo()));
      } else {
        processingDto.setUser(getUser(project.getCreatedBy()));
      }
    }
  }

  private static User getUser(Long id) {
    return User.builder().id(id).build();
  }

  public void sendMails(Collection<ProcessingDto> processingList) {
    Set<Long> userIds =
        processingList.stream()
            .map(ProcessingDto::getUser)
            .map(User::getId)
            .collect(Collectors.toSet());
    var users =
        this.profileFeignClient.getProjectOwners(userIds.stream().map(String::valueOf).toList());
    List<MailRequest> mailRequests = new ArrayList<>();

    processingList.forEach(
        (ProcessingDto processingDto) -> {
          var project = processingDto.getProject();
          users.stream()
              .filter(user -> Objects.equals(processingDto.getUser().getId(), user.getId()))
              .findFirst()
              .ifPresent((User user) -> buildMailModels(mailRequests, project, user));
        });

    // Prevent project from another company
    var logos = mailRequests.stream().map(MailRequest::getLogo).collect(Collectors.toSet());
    var logo = logos.size() == 1 ? logos.stream().iterator().next() : null;

    Executors.newSingleThreadExecutor()
        .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, logo));
  }

  private void buildMailModels(List<MailRequest> mailRequests, Project project, User user) {
    var company = project.getCorporateInfo();
    var model =
        new EmailParametersModel(
            user.getFullName(),
            project.getName(),
            null,
            "Échec de la validation du numéro de téléphone",
            null,
            user.getEmail());
    var request = new FailedValidatePhoneMailModel(model, null, company.getMainColor());
    var mailRequest = request.getMailRequest(templateEngine);
    mailRequest.setLogo(company.getLogo());
    mailRequests.add(mailRequest);
  }

  public void updateDocumentsStatus(List<Long> blockUsers) {
    Executors.newSingleThreadExecutor()
        .execute(
            () ->
                this.projectFeignClient.updateDocumentsStatus(
                    blockUsers, ProcessStatus.OTP_BLOCKED));
  }

  private static boolean isAdvanced(String signatureLevel) {
    return Objects.equals(signatureLevel, SignatureSettingLevel.ADVANCE.getValue());
  }
}
