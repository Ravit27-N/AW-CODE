package com.innovationandtrust.process.service;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.EmailDefaultValue;
import com.innovationandtrust.process.model.email.EmailInvitationRequest;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  private final ImpersonateTokenService impersonateTokenService;
  private final NotificationFeignClient notificationFeignClient;
  private final ProcessControlProperty processControlProperty;

  public EmailService(
      ImpersonateTokenService impersonateTokenService,
      NotificationFeignClient notificationFeignClient,
      ProcessControlProperty processControlProperty) {
    this.impersonateTokenService = impersonateTokenService;
    this.notificationFeignClient = notificationFeignClient;
    this.processControlProperty = processControlProperty;
  }

  public EmailInvitationRequest prepareSignCompleteMail(
      Project project, Participant participant, CorporateInfo corporateInfo) {
    var link =
        getLink(
            corporateInfo.getCompanyUuid(),
            participant,
            project.getFlowId(),
            processControlProperty.getSignCompletedContextPath());
    var request = this.prepareEmailInvitation(project, participant, corporateInfo, link);
    // For get the default subject
    request.setSubject(null);
    return request;
  }

  public EmailInvitationRequest prepareParticipantMail(
      Project project, Participant participant, CorporateInfo corporateInfo) {
    var link =
        getLink(
            corporateInfo.getCompanyUuid(),
            participant,
            project.getFlowId(),
            processControlProperty.getInvitationContextPath());
    return this.prepareEmailInvitation(project, participant, corporateInfo, link);
  }

  public EmailInvitationRequest prepareVideoVerifiedMail(
      Project project, Participant participant, CorporateInfo corporateInfo) {
    String link =
        getLink(
            corporateInfo.getCompanyUuid(),
            participant,
            project.getFlowId(),
            processControlProperty.getVideoVerifiedContextPath());
    return this.prepareVideoVerifyEmailInvitation(project, participant, corporateInfo, link);
  }

  private EmailInvitationRequest prepareVideoVerifyEmailInvitation(
      Project project, Participant participant, CorporateInfo corporateInfo, String link) {
    String role = participant.getVideoVerifiedStatus();
    String invitationMessage = EmailDefaultValue.getMessage(role);
    String invitationSubject = EmailDefaultValue.getSubject(role);

    // Set value to EmailParametersModel
    EmailParametersModel emailParametersModel =
        new EmailParametersModel(
            participant.getFullName(),
            project.getName(),
            invitationMessage,
            invitationSubject,
            link,
            participant.getEmail());

    EmailInvitationRequest request =
        new EmailInvitationRequest(
            emailParametersModel, corporateInfo.getCompanyName(), corporateInfo.getMainColor());
    request.setRole(role);
    request.setExpireDate(project.getDetail().getExpireDate());
    return request;
  }

  private EmailInvitationRequest prepareEmailInvitation(
      Project project, Participant participant, CorporateInfo corporateInfo, String link) {
    var invitationMessage = project.getMessageByRole(participant.getRole());

    // Set value to EmailParametersModel
    EmailParametersModel emailParametersModel =
        new EmailParametersModel(
            participant.getFullName(),
            project.getName(),
            invitationMessage.getInvitationMessage(),
            invitationMessage.getInvitationSubject(),
            link,
            participant.getEmail());

    var request =
        new EmailInvitationRequest(
            emailParametersModel, corporateInfo.getCompanyName(), corporateInfo.getMainColor());
    request.setRole(participant.getRole());
    return request;
  }

  public void sendInvitationMail(List<MailRequest> mailRequests, String logo) {
    if (!mailRequests.isEmpty()) {
      Executors.newSingleThreadExecutor()
          .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, logo));
    }
  }

  public void sendInvitationMail(MailRequest mailRequest, String logo) {
    if (Objects.nonNull(mailRequest)) {
      Executors.newSingleThreadExecutor()
          .execute(() -> this.notificationFeignClient.sendMail(mailRequest, logo));
    }
  }

  private String getLink(
      String companyUuid, Participant participant, String flowId, String contextPath) {
    // Get completed encrypted link
    return this.impersonateTokenService.getTokenUrlParam(
        flowId,
        participant.getUuid(),
        processControlProperty.getFrontEndUrl(),
        contextPath,
        companyUuid);
  }
}
