package com.innovationandtrust.process.chain.handler.refuse;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.process.model.email.RefuseNotification;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.RefuseRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.TriggerKey;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** Refusing process handler. */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefusingProcessHandler extends AbstractExecutionHandler {

  private final TemplateEngine templateEngine;

  private final ProjectFeignClient projectFeignClient;

  private final CorporateProfileFeignClient corporateProfileFeignClient;

  private final ProfileFeignClient profileFeignClient;

  private final SchedulerHandler schedulerHandler;

  private final NotificationFeignClient notificationFeignClient;

  private final ApiNgFeignClientFacade apiNgFeignClient;

  private Resource logoFile;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    if (!Objects.equals(project.getStatus(), ProjectStatus.IN_PROGRESS.name())) {
      throw new InvalidRequestException(
          "Cannot refuse project which not " + ProjectStatus.IN_PROGRESS.name());
    }

    this.logoFile =
        corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());

    this.refuse(
        project,
        context.get(SignProcessConstant.PARTICIPANT_ID, String.class),
        context.get(SignProcessConstant.COMMENT, String.class));

    if (Objects.equals(project.getTemplate().getSignProcess(), ScenarioStep.COUNTER_SIGN)) {
      var triggerKey =
          TriggerKey.triggerKey(project.getFlowId(), project.getTemplate().getSignProcess().name());
      log.info("Un-scheduling project reminder:{}", project.getFlowId());
      this.schedulerHandler.unScheduledJob(triggerKey);
      log.info("Successfully un-schedule project reminder:{}", project.getFlowId());
    }

    project.setStatus(ProjectStatus.REFUSED.name());
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void refuse(Project project, String uuid, String comment) {
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            person -> {
              var tag =
                  person.isSigner()
                      ? project.getTemplate().getSignProcess().getVal()
                      : project.getTemplate().getApprovalProcess().getVal();

              this.apiNgFeignClient.refuseDocuments(
                  project.getSessionId(),
                  new RefuseRequest(person.getActorUrl(), project.getDocumentUrls(), tag, comment));

              this.projectFeignClient.updateProjectAfterSigned(
                  new ProjectAfterSignRequest(
                      new SignatoryRequest(person.getId(), DocumentStatus.REFUSED, comment),
                      List.of()));
              person.setComment(comment);
              person.setRefused(true);
              this.sendEmail(project, person);
            });
  }

  private void sendEmail(Project project, Participant participant) {
    var company = project.getCorporateInfo();
    List<MailRequest> mailRequests = new ArrayList<>();

    var user =
        !Objects.nonNull(project.getAssignedTo())
            ? this.profileFeignClient.findUserById(project.getCreatedBy())
            : this.profileFeignClient.findUserById(project.getAssignedTo());
    if (Objects.nonNull(user)) {

      // Set value to EmailParametersModel
      EmailParametersModel emailParametersModel =
          new EmailParametersModel(
              user.getFullName(), project.getName(), "", "", "", user.getEmail());

      var request =
          new RefuseNotification(
              emailParametersModel,
              participant.getFullName(),
              participant.getComment(),
              company.getCompanyName(),
              company.getMainColor(),
              this.logoFile);
      request.setEndUser(true);
      mailRequests.add(request.getMailRequest(templateEngine));
    }

    getSignatories(project)
        .forEach(
            signer -> {
              // Set value to EmailParametersModel
              EmailParametersModel emailParametersModel =
                  new EmailParametersModel("", project.getName(), "", "", "", signer.getEmail());

              var request =
                  new RefuseNotification(
                      emailParametersModel,
                      signer.getFullName(),
                      participant.getComment(),
                      company.getCompanyName(),
                      company.getMainColor(),
                      this.logoFile);
              request.setRefuser(Objects.equals(signer.getId(), participant.getId()));

              mailRequests.add(request.getMailRequest(templateEngine));
            });

    Executors.newSingleThreadExecutor()
        .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, company.getLogo()));
  }

  private List<Participant> getSignatories(Project project) {
    List<Participant> signatories = new ArrayList<>();
    if (project.getTemplate().isOrderSign() && project.isReadyToSign()) {
      signatories.addAll(
          project.getParticipants().stream()
              .filter(person -> person.isSigner() && person.isInvited())
              .toList());
    } else {
      signatories.addAll(project.getParticipants().stream().filter(Participant::isSigner).toList());
    }

    return signatories;
  }
}
