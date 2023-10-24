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
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.notification.feignclient.model.SmsRequest;
import com.innovationandtrust.utils.tinyurl.TinyUrlFeignClient;
import com.innovationandtrust.utils.tinyurl.model.TinyUrlCriterion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationReminderLauncherHandler extends AbstractExecutionHandler {

  private final TemplateEngine templateEngine;
  private final ProcessControlProperty processControlProperty;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final ImpersonateTokenService impersonateToken;
  private final TinyUrlFeignClient tinyUrlFeignClient;
  private final NotificationFeignClient notificationFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    log.info("Notification reminder channel: {}", project.getReminderChannel());

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    switch (NotificationChannel.getByChannel(project.getReminderChannel())) {
      case EMAIL -> this.sendEmailReminder(project);
      case SMS -> this.sendSmsReminder(project);
      default -> this.sendReminder(project);
    }
    return ExecutionState.NEXT;
  }

  private void sendEmailReminder(Project project) {
    var company = project.getCorporateInfo();
    var logo = this.corporateProfileFeignClient.viewFileContent(company.getLogo());
    var participants = new ArrayList<>(project.getReminderParticipants(ParticipantRole.APPROVAL));
    participants.addAll(project.getReminderParticipants(ParticipantRole.SIGNATORY));
    List<MailRequest> mailRequests = new ArrayList<>();

    participants.forEach(
        person -> {
          // Set value to EmailParametersModel
          EmailParametersModel emailParametersModel =
              new EmailParametersModel(
                  person.getFirstName(),
                  project.getName(),
                  "",
                  "",
                  getLink(company.getCompanyUuid(), person.getUuid(), project.getFlowId()),
                  person.getEmail());
          var request =
              new EmailInvitationReminderRequest(
                  emailParametersModel,
                  company.getCompanyName(),
                  company.getMainColor(),
                  logo,
                  project.getDetail().getExpireDate(),
                  person.isApprover());

          mailRequests.add(request.getMailRequest(templateEngine));
        });

    Executors.newSingleThreadExecutor()
        .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, company.getLogo()));
  }

  private void sendSmsReminder(Project project) {
    var company = project.getCorporateInfo();
    var participants = new ArrayList<>(project.getReminderParticipants(ParticipantRole.APPROVAL));
    participants.addAll(project.getReminderParticipants(ParticipantRole.SIGNATORY));
    List<SmsRequest> smsRequests = new ArrayList<>();

    participants.forEach(
        person -> {
          var invitation = project.getMessageByRole(person.getRole());
          TinyUrlCriterion tinyUrlCriterion =
              new TinyUrlCriterion(
                  getLink(company.getCompanyUuid(), person.getUuid(), project.getFlowId()));
          var template =
              person.isApprover()
                  ? InvitationTemplateConstant.APPROVE_SMS_REMINDER_TEMPLATE
                  : InvitationTemplateConstant.SIGN_SMS_REMINDER_TEMPLATE;
          var smsRequest =
              SmsRequest.builder()
                  .participant(person.getPhone())
                  .message(
                      new SmsReminderNotification(
                              person.getFirstName(),
                              project.getName(),
                              invitation.getInvitationMessage(),
                              project.getDetail().getExpireDate(),
                              this.tinyUrlFeignClient
                                  .shortenUrl(tinyUrlCriterion)
                                  .getData()
                                  .getTinyUrl())
                          .getMessage(templateEngine, template))
                  .build();

          smsRequests.add(smsRequest);
        });

    if (!smsRequests.isEmpty()) {
      Executors.newSingleThreadExecutor()
          .execute(() -> this.notificationFeignClient.sendSmsMultiple(smsRequests));
    }
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
