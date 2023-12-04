package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.EmailInvitationReminderRequest;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.process.model.sms.SmsReminderNotification;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.NotificationChannel;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.notification.feignclient.model.SmsRequest;
import com.innovationandtrust.utils.tinyurl.TinyUrlFeignClient;
import com.innovationandtrust.utils.tinyurl.model.TinyUrlCriterion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** This class about launching the scheduler for remind participants to sign documents. */
@Slf4j
@Component
public class NotificationReminderLauncherHandler extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final ProcessControlProperty processControlProperty;
  private final ImpersonateTokenService impersonateToken;
  private final TinyUrlFeignClient tinyUrlFeignClient;
  private final NotificationFeignClient notificationFeignClient;
  private final ValidateCorporateSettingHandler validateCorporateSettingHandler;

  /**
   * Contractor of the class.
   *
   * @param templateEngine configured template engine of thymeleaf
   * @param processControlProperty properties required for project processing
   * @param impersonateToken encrypted token service
   * @param tinyUrlFeignClient open feign tiny url for shorten long url
   * @param notificationFeignClient open feign signature-notification service
   * @param validateCorporateSettingHandler validator to validate company signature level
   */
  public NotificationReminderLauncherHandler(
      TemplateEngine templateEngine,
      ProcessControlProperty processControlProperty,
      ImpersonateTokenService impersonateToken,
      TinyUrlFeignClient tinyUrlFeignClient,
      NotificationFeignClient notificationFeignClient,
      ValidateCorporateSettingHandler validateCorporateSettingHandler) {
    this.templateEngine = templateEngine;
    this.processControlProperty = processControlProperty;
    this.impersonateToken = impersonateToken;
    this.tinyUrlFeignClient = tinyUrlFeignClient;
    this.notificationFeignClient = notificationFeignClient;
    this.validateCorporateSettingHandler = validateCorporateSettingHandler;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    log.info("Notification reminder channel: {}", project.getReminderChannel());

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.validateCorporateSettingHandler.execute(context);

    switch (NotificationChannel.getByChannel(project.getReminderChannel())) {
      case EMAIL -> this.sendEmailReminder(project);
      case SMS -> this.sendSmsReminder(project);
      default -> this.sendReminder(project);
    }
    return ExecutionState.NEXT;
  }

  private void sendEmailReminder(Project project) {
    var company = project.getCorporateInfo();
    var participants = new ArrayList<>(project.getReminderParticipants(ParticipantRole.APPROVAL));
    participants.addAll(project.getReminderParticipants(ParticipantRole.SIGNATORY));
    List<MailRequest> mailRequests = new ArrayList<>();

    participants.forEach(
        (Participant person) -> {
          var request = buildEmailModel(project, person);
          mailRequests.add(request.getMailRequest(templateEngine));
        });

    Executors.newSingleThreadExecutor()
        .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, company.getLogo()));
  }

  private EmailInvitationReminderRequest buildEmailModel(Project project, Participant person) {
    var company = project.getCorporateInfo();
    // Set value to EmailParametersModel
    var emailParametersModel =
        new EmailParametersModel(
            person.getFirstName(),
            project.getName(),
            "",
            "",
            getLink(company.getCompanyUuid(), person.getUuid(), project.getFlowId()),
            person.getEmail());

    return new EmailInvitationReminderRequest(
        emailParametersModel,
        company.getCompanyName(),
        company.getMainColor(),
        project.getDetail().getExpireDate(),
        person.isApprover());
  }

  private void sendSmsReminder(Project project) {

    var participants = new ArrayList<>(project.getReminderParticipants(ParticipantRole.APPROVAL));
    participants.addAll(project.getReminderParticipants(ParticipantRole.SIGNATORY));
    List<SmsRequest> smsRequests = new ArrayList<>();

    participants.forEach((Participant person) -> smsRequests.add(builSmsRequest(project, person)));

    if (!smsRequests.isEmpty()) {
      Executors.newSingleThreadExecutor()
          .execute(() -> this.notificationFeignClient.sendSmsMultiple(smsRequests));
    }
  }

  private SmsRequest builSmsRequest(Project project, Participant person) {
    final var company = project.getCorporateInfo();
    final var invitation = project.getMessageByRole(person.getRole());
    final var tinyUrlCriterion =
        new TinyUrlCriterion(
            getLink(company.getCompanyUuid(), person.getUuid(), project.getFlowId()));
    var template =
        person.isApprover()
            ? InvitationTemplateConstant.APPROVE_SMS_REMINDER_TEMPLATE
            : InvitationTemplateConstant.SIGN_SMS_REMINDER_TEMPLATE;
    return SmsRequest.builder()
        .participant(person.getPhone())
        .message(
            new SmsReminderNotification(
                    person.getFirstName(),
                    project.getName(),
                    invitation.getInvitationMessage(),
                    project.getDetail().getExpireDate(),
                    this.tinyUrlFeignClient.shortenUrl(tinyUrlCriterion).getData().getTinyUrl())
                .getMessage(templateEngine, template))
        .build();
  }

  private void sendReminder(Project project) {
    Executors.newSingleThreadExecutor().execute(() -> this.sendEmailReminder(project));
    Executors.newSingleThreadExecutor().execute(() -> this.sendSmsReminder(project));
  }

  private String getLink(String companyUuid, String uuid, String flowId) {
    // Get completed encrypted link
    return this.impersonateToken.getTokenUrlParam(
        flowId,
        uuid,
        processControlProperty.getFrontEndUrl(),
        processControlProperty.getInvitationContextPath(),
        companyUuid);
  }
}
