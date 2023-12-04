package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** This class handle on sending invitation to recipient. */
@Slf4j
@Component
public class RecipientInvitationHandler extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final ProjectFeignClient projectFeignClient;
  private final EmailService emailService;

  /**
   * Contractor of the class.
   *
   * @param templateEngine configured template engine of thymeleaf
   * @param emailService email template builder
   * @param projectFeignClient open feign endpoints of project-management service
   */
  public RecipientInvitationHandler(
      TemplateEngine templateEngine,
      ProjectFeignClient projectFeignClient,
      EmailService emailService) {
    this.templateEngine = templateEngine;
    this.projectFeignClient = projectFeignClient;
    this.emailService = emailService;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.inviteRecipient(project);

    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void inviteRecipient(Project project) {
    if (project.hasNextParticipant()) {
      return;
    }

    var company = project.getCorporateInfo();
    var recipients = new ArrayList<SignatoryRequest>();
    List<MailRequest> mailRequests = new ArrayList<>();

    project
        .getParticipantsToInviteBy(RoleConstant.ROLE_RECEIPT)
        .forEach(
            (Participant participant) -> {
              recipients.add(
                  new SignatoryRequest(
                      participant.getId(), InvitationStatus.SENT, participant.getUuid()));
              participant.setInvited(true);
              participant.setInvitationDate(Date.from(Instant.now()));

              var request = this.emailService.prepareParticipantMail(project, participant, company);
              request.setRole(participant.getRole());
              mailRequests.add(request.getMailRequest(templateEngine));
            });

    this.emailService.sendInvitationMail(mailRequests, project.getCorporateInfo().getLogo());
    this.projectFeignClient.updateStatus(project.getId(), recipients);
  }
}
