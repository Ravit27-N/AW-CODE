package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.chain.handler.sign.UploadSignedDocument;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** This class about process completing signing project. */
@Slf4j
@Component
public class CompleteSigningProcessHandler extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final EmailService emailService;
  private final ProjectFeignClient projectFeignClient;
  private final UploadSignedDocument uploadSignedDocument;

  /**
   * Contractor of the class.
   *
   * @param templateEngine configured template engine of thymeleaf
   * @param emailService email template builder
   * @param projectFeignClient open feign endpoints of project-management service
   * @param uploadSignedDocument class upload the final signed documents
   */
  public CompleteSigningProcessHandler(
      TemplateEngine templateEngine,
      EmailService emailService,
      ProjectFeignClient projectFeignClient,
      UploadSignedDocument uploadSignedDocument) {
    this.templateEngine = templateEngine;
    this.emailService = emailService;
    this.projectFeignClient = projectFeignClient;
    this.uploadSignedDocument = uploadSignedDocument;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    if (!project.hasNextParticipant()) {
      this.sendSignedDocuments(project);
    }

    this.completeProcess(context, project);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void completeProcess(ExecutionContext context, Project project) {
    log.info("Complete project is processing");
    final boolean isRefused = project.hasRefused();

    if (!project.hasNextParticipant() && !project.hasNextRecipient()) {
      log.info("Update project after all signatures");
      project.setStatus(isRefused ? ProjectStatus.REFUSED.name() : ProjectStatus.COMPLETED.name());
      this.projectFeignClient.completeProjectWithStatus(project.getId(), project.getStatus());
      this.uploadSignedDocument.execute(context);
    }

    if (isRefused) {
      this.projectRefused(context, project);
    }
  }

  private void projectRefused(ExecutionContext context, Project project) {
    if (Objects.equals(project.getTemplate().getSignProcess(), ScenarioStep.COUNTER_SIGN)) {
      log.info("Upload manifest when refuse of counter-sign");
      this.uploadSignedDocument.execute(context);
    }

    log.info("Update project to refused");
    project.setStatus(ProjectStatus.REFUSED.name());
    this.projectFeignClient.completeProjectWithStatus(project.getId(), project.getStatus());
  }

  private void sendSignedDocuments(Project project) {
    final var company = project.getCorporateInfo();
    List<MailRequest> mailRequests = new ArrayList<>();

    project
        .getParticipantsByRole(ParticipantRole.SIGNATORY.getRole())
        .forEach(
            (Participant person) -> {
              var mailRequest = this.emailService.prepareSignCompleteMail(project, person, company);
              // Change role, because invitation mail template depends on the role
              mailRequest.setRole(ProcessStatus.SIGN_COMPLETED);
              mailRequest.setFullName(person.getFullName());
              mailRequests.add(mailRequest.getMailRequest(templateEngine));
            });
    this.emailService.sendInvitationMail(mailRequests, company.getLogo());
  }
}
