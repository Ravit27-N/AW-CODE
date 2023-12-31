package com.innovationandtrust.process.chain.handler.expired;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.process.model.email.ModificationProjectEmailRequest;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.Session;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.date.DateUtil;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** Update project expiration date and send confirmation mail. */
@Slf4j
@Component
public class UpdateProjectHandler extends AbstractExecutionHandler {
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final TemplateEngine templateEngine;
  private final ProcessControlProperty processControlProperty;
  private final ImpersonateTokenService impersonateToken;
  private final NotificationFeignClient notificationFeignClient;
  private final SettingProperties settingProperties;
  private final ProjectFeignClient projectFeignClient;

  public UpdateProjectHandler(
      ApiNgFeignClientFacade apiNgFeignClient,
      CorporateProfileFeignClient corporateProfileFeignClient,
      TemplateEngine templateEngine,
      ProcessControlProperty processControlProperty,
      ImpersonateTokenService impersonateToken,
      NotificationFeignClient notificationFeignClient,
      SettingProperties settingProperties,
      ProjectFeignClient projectFeignClient) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.templateEngine = templateEngine;
    this.processControlProperty = processControlProperty;
    this.impersonateToken = impersonateToken;
    this.notificationFeignClient = notificationFeignClient;
    this.settingProperties = settingProperties;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var newExpireDate = context.get(SignProcessConstant.NEW_EXPIRE_DATE, Date.class);

    ProcessControlUtils.checkIsCanceled(project.getStatus());

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    if (newExpireDate.compareTo(project.getDetail().getExpireDate()) > 0) {
      if (Objects.nonNull(project.getSessionId())) {
        this.updateSessionTtl(project, newExpireDate);
      }
      this.sendUpdatedMail(project);
      context.put(SignProcessConstant.PROJECT_KEY, project);
      context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.PROJECT_EXTENDED_EXPIRE);
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    } else {
      return ExecutionState.END;
    }

    return ExecutionState.NEXT;
  }

  private void updateSessionTtl(Project project, Date newExpireDate) {
    this.checkCreatedDate(project);
    DateUtil.validateExpirationDate(
        newExpireDate,
        settingProperties.getProjectMaxExpire(),
        settingProperties.getProjectMaxHours());

    final var createdDate = DateUtil.plushHours(project.getCreatedAt(), 1).getTime();
    final var newExpire =
        DateUtil.getCalendar(project.getDetail().getExpireDate()).getTimeInMillis();

    final var newTtl = (int) TimeUnit.MILLISECONDS.toSeconds(newExpire - createdDate);

    log.info("Extending project expiration date...");
    this.apiNgFeignClient.extendSession(project.getSessionId(), new Session(newTtl));
    project.getDetail().setExpireDate(newExpireDate);
  }

  private void sendUpdatedMail(Project project) {
    List<Participant> participants = getParticipants(project);
    var company = project.getCorporateInfo();
    var logo = this.corporateProfileFeignClient.viewFileContent(company.getLogo());
    List<MailRequest> mailRequests = new ArrayList<>();

    participants.forEach(
        person -> {
          log.info("Preparing mail for {} email: {}", person.getFullName(), person.getEmail());

          // Set value to EmailParametersModel
          EmailParametersModel emailParametersModel =
              new EmailParametersModel(
                  person.getFullName(),
                  project.getName(),
                  "",
                  "",
                  getLink(company.getCompanyUuid(), person.getUuid(), project.getFlowId()),
                  person.getEmail());

          var request =
              new ModificationProjectEmailRequest(
                  emailParametersModel,
                  company.getCompanyName(),
                  company.getMainColor(),
                  project.getDetail().getExpireDate(),
                  person.isApprover());

          mailRequests.add(request.getMailRequest(templateEngine));
        });

    Executors.newSingleThreadExecutor()
        .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, company.getLogo()));
  }

  private List<Participant> getParticipants(Project project) {
    List<Participant> participants;

    boolean isOrderApprove = project.getTemplate().isOrderApprove();
    boolean isOrderSign = project.getTemplate().isOrderSign();
    boolean bothEnable = isOrderSign && isOrderApprove;

    if (isOrderApprove && !isOrderSign) {
      participants = new ArrayList<>(getOrderApprove(project));

      if (project.isReadyToSign()) {
        participants.addAll(
            project.getParticipants().stream().filter(Participant::isSigner).toList());
      }
    } else if (!isOrderApprove && isOrderSign) {
      participants =
          new ArrayList<>(
              project.getParticipants().stream().filter(Participant::isApprover).toList());

      if (project.isReadyToSign()) {
        participants.addAll(getOrderSigner(project));
      }
    } else if (bothEnable) {
      participants = new ArrayList<>(getOrderApprove(project));

      if (project.isReadyToSign()) {
        participants.addAll(getOrderSigner(project));
      }
    } else {
      participants =
          new ArrayList<>(
              project.getParticipants().stream().filter(Participant::isApprover).toList());

      if (project.isReadyToSign()) {
        participants.addAll(
            project.getParticipants().stream().filter(Participant::isSigner).toList());
      }
    }

    return participants;
  }

  private List<Participant> getOrderApprove(Project project) {
    var participants =
        new ArrayList<>(
            project.getParticipants().stream()
                .filter(Participant::isApprover)
                .filter(person -> person.isInvited() || person.isApproved())
                .toList());
    if (!participants.isEmpty() && (participants.get(participants.size() - 1).isApproved())) {
      project
          .getParticipantToInviteWithOrder()
          .ifPresent(
              person -> {
                if (!person.isSigner()) {
                  participants.add(person);
                }
              });
    }

    return participants;
  }

  private List<Participant> getOrderSigner(Project project) {
    var participants =
        new ArrayList<>(
            project.getParticipants().stream()
                .filter(Participant::isSigner)
                .filter(person -> person.isInvited() || person.isSigned())
                .toList());
    if (!participants.isEmpty() && (participants.get(participants.size() - 1).isSigned())) {
      project.getParticipantToInviteWithOrder().ifPresent(participants::add);
    }

    return participants;
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

  private void checkCreatedDate(Project project) {
    // project before 10/10/2023 has no property createdAt
    if (Objects.isNull(project.getCreatedAt())) {
      var foundProject = this.projectFeignClient.findExternalById(project.getId());
      project.setCreatedAt(foundProject.getCreatedAt());
    }
  }
}
