package com.innovationandtrust.process.chain.handler.expired;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.ExpiredProjectMailModel;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectExpiredLauncherHandler extends AbstractExecutionHandler {

  private final ProjectFeignClient projectFeignClient;
  private final NotificationFeignClient notificationFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final TemplateEngine templateEngine;
  private String projectName;
  private Resource logoFile;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var group = context.get(SignProcessConstant.JOB_GROUP, String.class);
    this.projectName = project.getName();

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.logoFile =
        this.corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());

    if (Objects.equals(project.getStatus(), ProjectStatus.IN_PROGRESS.name())
        || Objects.equals(project.getStatus(), ProjectStatus.URGENT.name())) {
      if (Objects.equals(group, ProjectExpiredScheduleHandler.EXPIRE)) {
        project.setStatus(ProjectStatus.EXPIRED.name());
        this.projectFeignClient.updateProjectStatusExpired(project.getId());
        log.info("Preparing for mail request...");

        List<Participant> participants;

        if (Objects.requireNonNull(project.getTemplate().getSignProcess()) == ScenarioStep.COUNTER_SIGN) {
          participants =
                  project.getParticipantsByRoleAndIsInvited(ParticipantRole.SIGNATORY.getRole(), true);
        } else {
          participants =
                  project.getParticipantsByRole(ParticipantRole.SIGNATORY.getRole());
        }
        this.sendMails(participants, project);

      }


      context.put(SignProcessConstant.PROJECT_KEY, project);
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    }
    return ExecutionState.NEXT;
  }

  private void sendMails(List<Participant> participants, Project project) {
    if (!participants.isEmpty()) {
      var company = project.getCorporateInfo();
      List<MailRequest> mailRequests = new ArrayList<>();

      participants.forEach(
          person -> {
            var request =
                person.isApprover()
                    ? null
                    : new ExpiredProjectMailModel(
                        person.getFirstName(),
                        this.projectName,
                        "Le délai de signature pour signer les documents du projet",
                        person.getEmail(),
                        company.getCompanyName(),
                        company.getMainColor(),
                        this.logoFile);

            if (Objects.nonNull(request)) {
              mailRequests.add(request.getMailRequest(templateEngine));
            }
          });

      Executors.newSingleThreadExecutor()
          .execute(
              () -> {
                if (!mailRequests.isEmpty()) {
                  log.info("Requesting to notification...");
                  this.notificationFeignClient.sendMultiple(mailRequests, company.getLogo());
                  log.info("Requested end...");
                }
              });
    }
  }
}
