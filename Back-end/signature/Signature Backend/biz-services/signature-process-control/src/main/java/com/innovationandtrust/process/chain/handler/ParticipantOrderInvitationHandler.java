package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** This class about participant invitation to involved project by ordering. */
@Slf4j
@Component
public class ParticipantOrderInvitationHandler extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final ProjectFeignClient projectFeignClient;
  private final EmailService emailService;
  private final ValidateCorporateSettingHandler validateCorporateSettingHandler;

  /**
   * Contractor of the class.
   *
   * @param templateEngine configured template engine of thymeleaf
   * @param emailService email template builder
   * @param validateCorporateSettingHandler validator to validate company signature level
   * @param projectFeignClient open feign endpoints of project-management service
   */
  public ParticipantOrderInvitationHandler(
      TemplateEngine templateEngine,
      ProjectFeignClient projectFeignClient,
      EmailService emailService,
      ValidateCorporateSettingHandler validateCorporateSettingHandler) {
    this.templateEngine = templateEngine;
    this.projectFeignClient = projectFeignClient;
    this.emailService = emailService;
    this.validateCorporateSettingHandler = validateCorporateSettingHandler;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = ProcessControlUtils.getProject(context);

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.validateCorporateSettingHandler.execute(context);

    if (!project.getTemplate().isOrderApprove()) {
      this.sendApprovalInvitation(project);
      if (project.isReadyToSign()) {
        this.sendSignInvitation(project);
      }
    } else {
      this.sendSignInvitation(project);
    }

    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void sendSignInvitation(Project project) {
    var company = project.getCorporateInfo();
    var participantOtp = project.getParticipantToInviteWithOrder();
    if (participantOtp.isPresent()) {
      var person = participantOtp.get();
      person.setInvited(true);
      person.setInvitationDate(Date.from(Instant.now()));

      log.info("The next person to invite {}", person.getFullName());
      if (!person.isEndUser()) {
        Executors.newSingleThreadExecutor()
            .execute(
                () -> {
                  if (person.isSigner() || person.isApprover()) {
                    var request =
                        this.emailService.prepareParticipantMail(project, person, company);

                    request.setExpireDate(project.getDetail().getExpireDate());
                    request.setRole(person.getRole());
                    this.emailService.sendInvitationMail(
                        request.getMailRequest(templateEngine), company.getLogo());
                  }
                });
      } else {
        log.info("This person {} is an end user ...!", person.getFullName());
      }
      this.updateParticipants(project.getId(), List.of(person));
    }
  }

  private void sendApprovalInvitation(Project project) {
    var participants = project.getParticipantsToInviteBy(ParticipantRole.APPROVAL.getRole());
    List<Participant> participantsToUpdate = new ArrayList<>();
    List<MailRequest> mailRequests = new ArrayList<>();

    participants.forEach((Participant person) -> buildMailRequest(mailRequests, project, person));

    this.emailService.sendInvitationMail(mailRequests, project.getCorporateInfo().getLogo());

    this.updateParticipants(project.getId(), participantsToUpdate);
  }

  private void buildMailRequest(
      Collection<MailRequest> mailRequests, Project project, Participant person) {
    person.setInvited(true);
    person.setInvitationDate(Date.from(Instant.now()));
    final var company = project.getCorporateInfo();
    var request = this.emailService.prepareParticipantMail(project, person, company);

    if (Objects.nonNull(request)) {
      request.setRole(person.getRole());
      request.setExpireDate(project.getDetail().getExpireDate());
      mailRequests.add(request.getMailRequest(templateEngine));
    }
  }

  private void updateParticipants(Long projectId, List<Participant> participants) {
    Executors.newSingleThreadExecutor()
        .execute(
            () ->
                this.projectFeignClient.updateStatus(
                    projectId,
                    participants.stream()
                        .map(
                            person ->
                                new SignatoryRequest(
                                    person.getId(), InvitationStatus.SENT, person.getUuid()))
                        .toList()));
  }
}
