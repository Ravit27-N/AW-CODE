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
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

/** This class handle on invitation project viewers. */
@Slf4j
@Component
public class ViewerInvitationHandler extends AbstractExecutionHandler {

  private final TemplateEngine templateEngine;
  private final ProjectFeignClient projectFeignClient;
  private final EmailService emailService;
  private final ValidateCorporateSettingHandler validateCorporateSettingHandler;

  /**
   * Contractor of the class.
   *
   * @param templateEngine configured template engine of thymeleaf
   * @param emailService email template builder
   * @param projectFeignClient open feign endpoints of project-management service
   * @param validateCorporateSettingHandler handler handle on validation corporate signature level
   */
  public ViewerInvitationHandler(
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
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.validateCorporateSettingHandler.execute(context);

    this.sendInvitation(project);
    return ExecutionState.NEXT;
  }

  private void sendInvitation(Project project) {
    var participants = project.getParticipantsToInviteBy(ParticipantRole.VIEWER.getRole());
    if (!participants.isEmpty()) {

      var company = project.getCorporateInfo();
      List<MailRequest> mailRequests = new ArrayList<>();

      participants.forEach(
          (Participant person) -> {
            person.setInvited(true);
            person.setInvitationDate(Date.from(Instant.now()));

            var request = this.emailService.prepareParticipantMail(project, person, company);
            mailRequests.add(request.getMailRequest(templateEngine));
          });

      this.emailService.sendInvitationMail(mailRequests, project.getCorporateInfo().getLogo());

      var signatories =
          participants.stream()
              .map(person -> new SignatoryRequest(person.getId(), InvitationStatus.SENT))
              .toList();
      this.projectFeignClient.updateStatus(project.getId(), signatories);
    }
  }
}
